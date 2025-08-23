@file:Suppress(
    "TooManyFunctions",
    "ThrowsCount",
    "LoopWithTooManyJumpStatements",
    "MagicNumber",
    "ReturnCount",
    "TooGenericExceptionCaught",
    "SwallowedException",
    "CyclomaticComplexMethod",
    "NestedBlockDepth",
    "MaxLineLength",
)
package ly.neptune.nexus.fcms.requests.internal

import kotlinx.coroutines.delay
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.core.http.FcmsHttpException
import ly.neptune.nexus.fcms.core.http.JsonSupport
import ly.neptune.nexus.fcms.core.http.OkHttpProvider
import ly.neptune.nexus.fcms.requests.FcmsRequestsClient
import ly.neptune.nexus.fcms.requests.model.PurchaseRequestQueueItem
import ly.neptune.nexus.fcms.salaries.model.Page
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException
import java.net.HttpURLConnection
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ThreadLocalRandom

internal class FcmsRequestsClientImpl(
    private val config: FcmsConfig,
) : FcmsRequestsClient {

    private val client = OkHttpProvider.create(config)
    private val json = JsonSupport.mapper

    override suspend fun listPendingRequests(
        page: Int?,
        options: RequestOptions?
    ): Page<PurchaseRequestQueueItem> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/purchase-requests-queue")
            if (page != null) append("?page=").append(page)
        }
        val req = Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", config.userAgent)
            .applyAuth(options)
            .applyReadOverride(options)
            .build()
        val body = executeWithRetries(req, isIdempotent = true)
        body.use { rb ->
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), PurchaseRequestQueueItem::class.java)
            return Page(
                data = pr.data,
                total = pr.total,
                perPage = pr.perPage,
                currentPage = pr.currentPage,
                next = pr.next,
                prev = pr.prev,
            )
        }
    }

    private fun Request.Builder.applyAuth(options: RequestOptions?): Request.Builder {
        val override = options?.tokenOverride
        val value = if (!override.isNullOrBlank()) {
            if (override.startsWith("Bearer ", ignoreCase = true)) override else "Bearer $override"
        } else {
            val t = config.tokenProvider.invoke()
            if (t.startsWith("Bearer ", ignoreCase = true)) t else "Bearer $t"
        }
        return this.header("Authorization", value)
    }

    private fun Request.Builder.applyReadOverride(options: RequestOptions?): Request.Builder {
        val ms = options?.readTimeoutMillisOverride
        if (ms != null) {
            this.header("X-Read-Timeout-Millis", ms.toString())
        }
        return this
    }

    private fun effectiveBaseUrl(options: RequestOptions?): String {
        val candidate = options?.baseUrlOverride?.takeUnless { it.isBlank() } ?: config.baseUrl
        return candidate.trimEnd('/')
    }

    private suspend fun executeWithRetries(
        req: Request,
        isIdempotent: Boolean
    ): okhttp3.ResponseBody {
        var attempt = 0
        val enable = config.enableRetries && (isIdempotent)
        val max = if (enable) config.maxRetries.coerceAtLeast(0) else 0
        while (true) {
            try {
                val call = client.newCall(req)
                val resp = call.execute()
                if (resp.isSuccessful) {
                    return resp.body ?: run {
                        resp.closeQuietly()
                        throw IOException("Empty response body")
                    }
                }
                // Non-2xx
                val ex = toHttpException(resp)
                resp.closeQuietly()
                if (enable && shouldRetry(ex.status)) {
                    val delayMs = computeBackoff(attempt, ex.retryAfterSeconds)
                    if (attempt < max) {
                        attempt++
                        delay(delayMs)
                        continue
                    }
                }
                throw ex
            } catch (io: IOException) {
                if (enable && attempt < max) {
                    attempt++
                    val delayMs = computeBackoff(attempt, null)
                    delay(delayMs)
                    continue
                }
                throw io
            }
        }
    }

    private fun shouldRetry(status: Int): Boolean = when (status) {
        HttpURLConnection.HTTP_CLIENT_TIMEOUT, // 408
        429, // Too Many Requests
        HttpURLConnection.HTTP_UNAVAILABLE, // 503
        HttpURLConnection.HTTP_GATEWAY_TIMEOUT // 504
        -> true
        else -> false
    }

    private fun computeBackoff(attempt: Int, retryAfterSeconds: Long?): Long {
        val base = if (retryAfterSeconds != null) Duration.ofSeconds(retryAfterSeconds).toMillis() else 250L
        val exp = (1L shl attempt).coerceAtMost(64) // exponential cap
        val maxDelay = (base * exp).coerceAtMost(config.maxRetryDelayMillis)
        val jitter = ThreadLocalRandom.current().nextLong(maxDelay / 2, maxDelay + 1)
        return jitter
    }

    private fun toHttpException(resp: Response): FcmsHttpException {
        val status = resp.code
        val headers = resp.headers.toMultimap().mapValues { it.value.joinToString(",") }
        val bodyStr = try {
            resp.body?.string()
        } catch (e: Exception) {
            null
        }
        var code: String? = null
        var message: String? = null
        if (!bodyStr.isNullOrBlank()) {
            try {
                val node = json.readTree(bodyStr)
                message = node.get("message")?.asText() ?: message
                code = node.get("code")?.asText() ?: code
            } catch (_: Exception) {
                // ignore JSON parse errors for body
            }
        }
        val retryAfterHeader = resp.header("Retry-After")
        val retryAfterSeconds = parseRetryAfter(retryAfterHeader)
        return FcmsHttpException(status, code, message, bodyStr, headers, retryAfterSeconds)
    }

    private fun parseRetryAfter(value: String?): Long? {
        if (value.isNullOrBlank()) return null
        // Either seconds or HTTP-date
        value.toLongOrNull()?.let { return it }
        return try {
            val dt = OffsetDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME)
            val now = OffsetDateTime.now()
            val diff = java.time.Duration.between(now, dt).seconds
            if (diff > 0) diff else 0
        } catch (_: Exception) {
            null
        }
    }

    override fun close() {
        // Nothing specific
    }
}
