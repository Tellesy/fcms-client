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

package ly.neptune.nexus.fcms.fxhousesclient.miscs.internal

import kotlinx.coroutines.delay
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.core.http.FcmsHttpException
import ly.neptune.nexus.fcms.fxhousesclient.core.http.JsonSupport
import ly.neptune.nexus.fcms.fxhousesclient.core.http.OkHttpProvider
import ly.neptune.nexus.fcms.fxhousesclient.miscs.FcmsFxMiscsClient
import ly.neptune.nexus.fcms.fxhousesclient.model.CodeName
import ly.neptune.nexus.fcms.fxhousesclient.model.ExchangeRate
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException
import java.net.HttpURLConnection
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ThreadLocalRandom

internal class FcmsFxMiscsClientImpl(
    private val config: FcmsFxConfig,
) : FcmsFxMiscsClient {

    private val managed = OkHttpProvider.createManaged(config)
    private val client = managed.client
    private val json = JsonSupport.mapper

    override suspend fun listExchangeRates(dateFilter: String?, options: RequestOptions?): List<ExchangeRate> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/miscs/exchange-rates")
            if (!dateFilter.isNullOrBlank()) {
                append("?filter[date]=").append(dateFilter)
            }
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
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), ExchangeRate::class.java)
            return pr.data
        }
    }

    override suspend fun listDeclineReasons(options: RequestOptions?): List<CodeName> =
        listCodeName("/api/v1/miscs/decline-reasons", options)

    override suspend fun listPurchaseRequestStates(options: RequestOptions?): List<CodeName> =
        listCodeName("/api/v1/miscs/purchase-request-states", options)

    override suspend fun listPurchaseRequestTypes(options: RequestOptions?): List<CodeName> =
        listCodeName("/api/v1/miscs/purchase-request-types", options)

    private suspend fun listCodeName(path: String, options: RequestOptions?): List<CodeName> {
        val base = effectiveBaseUrl(options)
        val url = "$base$path"
        val req = Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", config.userAgent)
            .applyAuth(options)
            .applyReadOverride(options)
            .build()
        val body = executeWithRetries(req, isIdempotent = true)
        body.use { rb ->
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), CodeName::class.java)
            return pr.data
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
        if (ms != null) this.header("X-Read-Timeout-Millis", ms.toString())
        return this
    }

    private fun effectiveBaseUrl(options: RequestOptions?): String {
        val candidate = options?.baseUrlOverride?.takeUnless { it.isBlank() } ?: config.baseUrl
        return candidate.trimEnd('/')
    }

    private suspend fun executeWithRetries(req: Request, isIdempotent: Boolean): okhttp3.ResponseBody {
        var attempt = 0
        val enable = config.enableRetries && isIdempotent
        val max = if (enable) config.maxRetries.coerceAtLeast(0) else 0
        while (true) {
            try {
                val call = client.newCall(req)
                val resp = call.execute()
                if (resp.isSuccessful) {
                    return resp.body ?: run {
                        resp.closeQuietly(); throw IOException("Empty response body")
                    }
                }
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
                    delay(computeBackoff(attempt, null))
                    continue
                }
                throw io
            }
        }
    }

    private fun shouldRetry(status: Int): Boolean = when (status) {
        HttpURLConnection.HTTP_CLIENT_TIMEOUT, 429, HttpURLConnection.HTTP_UNAVAILABLE, HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> true
        else -> false
    }

    private fun computeBackoff(attempt: Int, retryAfterSeconds: Long?): Long {
        val base = if (retryAfterSeconds != null) Duration.ofSeconds(retryAfterSeconds).toMillis() else 250L
        val exp = (1L shl attempt).coerceAtMost(64)
        val maxDelay = (base * exp).coerceAtMost(config.maxRetryDelayMillis)
        return ThreadLocalRandom.current().nextLong(maxDelay / 2, maxDelay + 1)
    }

    private fun toHttpException(resp: Response): FcmsHttpException {
        val status = resp.code
        val headers = resp.headers.toMultimap().mapValues { it.value.joinToString(",") }
        val bodyStr = try { resp.body?.string() } catch (_: Exception) { null }
        var code: String? = null
        var message: String? = null
        if (!bodyStr.isNullOrBlank()) {
            try {
                val node = json.readTree(bodyStr)
                val err = node.get("error")
                if (err != null && err.isObject) {
                    code = err.get("code")?.asText()
                    message = err.get("message")?.asText()
                }
                if (code == null) code = node.get("code")?.asText()
                if (message == null) message = node.get("message")?.asText()
            } catch (_: Exception) {
                // ignore
            }
        }
        val retryAfterHeader = resp.header("Retry-After")
        val retryAfterSeconds = parseRetryAfter(retryAfterHeader)
        return FcmsHttpException(status, code, message, bodyStr, headers, retryAfterSeconds)
    }

    private fun parseRetryAfter(value: String?): Long? {
        if (value.isNullOrBlank()) return null
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
        managed.close()
    }
}
