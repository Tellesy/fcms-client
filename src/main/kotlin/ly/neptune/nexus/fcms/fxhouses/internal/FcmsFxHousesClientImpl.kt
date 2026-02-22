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

package ly.neptune.nexus.fcms.fxhouses.internal

import kotlinx.coroutines.delay
import ly.neptune.nexus.fcms.accounts.model.BankAccount
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.core.http.FcmsHttpException
import ly.neptune.nexus.fcms.core.http.JsonSupport
import ly.neptune.nexus.fcms.core.http.OkHttpProvider
import ly.neptune.nexus.fcms.fxhouses.FcmsFxHousesClient
import ly.neptune.nexus.fcms.fxhouses.model.FxContract
import ly.neptune.nexus.fcms.fxhouses.model.FxContractsListFilter
import ly.neptune.nexus.fcms.fxhouses.model.FxHouse
import ly.neptune.nexus.fcms.fxhouses.model.FxPurchaseRequest
import ly.neptune.nexus.fcms.fxhouses.model.FxPurchaseRequestsListFilter
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractActionRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractDeclineRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractProcessRequest
import ly.neptune.nexus.fcms.salaries.model.Page
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException
import java.net.HttpURLConnection
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ThreadLocalRandom

internal class FcmsFxHousesClientImpl(
    private val config: FcmsConfig,
) : FcmsFxHousesClient {

    private val managed = OkHttpProvider.createManaged(config)
    private val client = managed.client
    private val json = JsonSupport.mapper
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    override suspend fun listBankAccounts(page: Int?, options: RequestOptions?): Page<BankAccount> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/bank-accounts")
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
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), BankAccount::class.java)
            return Page(pr.data, pr.total, pr.perPage, pr.currentPage, pr.next, pr.prev)
        }
    }

    override suspend fun listFxHouses(page: Int?, options: RequestOptions?): Page<FxHouse> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/fx-houses")
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
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), FxHouse::class.java)
            return Page(pr.data, pr.total, pr.perPage, pr.currentPage, pr.next, pr.prev)
        }
    }

    override suspend fun listContracts(
        page: Int?,
        filter: FxContractsListFilter?,
        options: RequestOptions?
    ): Page<FxContract> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/fx-houses/contracts")
            var first = true
            fun addParam(k: String, v: String?) {
                if (v.isNullOrBlank()) return
                append(if (first) "?" else "&")
                first = false
                append(k).append("=").append(v)
            }
            if (page != null) addParam("page", page.toString())
            if (filter != null) {
                addParam("filter[date_from]", filter.dateFrom)
                addParam("filter[date_to]", filter.dateTo)
                addParam("filter[state]", filter.state)
                addParam("filter[cbl_key]", filter.cblKey)
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
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), FxContract::class.java)
            return Page(pr.data, pr.total, pr.perPage, pr.currentPage, pr.next, pr.prev)
        }
    }

    override suspend fun listFxPurchaseRequests(
        page: Int?,
        filter: FxPurchaseRequestsListFilter?,
        options: RequestOptions?
    ): Page<FxPurchaseRequest> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/fx-houses/purchase-requests")
            var first = true
            fun addParam(k: String, v: String?) {
                if (v.isNullOrBlank()) return
                append(if (first) "?" else "&")
                first = false
                append(k).append("=").append(v)
            }
            if (page != null) addParam("page", page.toString())
            if (filter != null) {
                addParam("filter[approved_on]", filter.approvedOn)
                addParam("filter[state]", filter.state)
                addParam("filter[type]", filter.type)
                addParam("filter[reference]", filter.reference)
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
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), FxPurchaseRequest::class.java)
            return Page(pr.data, pr.total, pr.perPage, pr.currentPage, pr.next, pr.prev)
        }
    }

    override suspend fun approveContract(uuid: String, request: FxContractActionRequest, options: RequestOptions?): FxContract {
        val base = effectiveBaseUrl(options)
        val url = "$base/api/v1/fx-houses/contracts/$uuid/approve"
        val payload = json.writeValueAsString(request)
        val req = Request.Builder()
            .url(url)
            .patch(payload.toRequestBody(jsonMedia))
            .header("User-Agent", config.userAgent)
            .applyAuth(options)
            .applyReadOverride(options)
            .build()
        val body = executeWithRetries(req, isIdempotent = false)
        body.use { rb ->
            return JsonSupport.readSingleEnvelope(rb.byteStream(), FxContract::class.java)
        }
    }

    override suspend fun processContract(uuid: String, request: FxContractProcessRequest, options: RequestOptions?): FxContract {
        val base = effectiveBaseUrl(options)
        val url = "$base/api/v1/fx-houses/contracts/$uuid/process"
        val payload = json.writeValueAsString(request)
        val req = Request.Builder()
            .url(url)
            .patch(payload.toRequestBody(jsonMedia))
            .header("User-Agent", config.userAgent)
            .applyAuth(options)
            .applyReadOverride(options)
            .build()
        val body = executeWithRetries(req, isIdempotent = false)
        body.use { rb ->
            return JsonSupport.readSingleEnvelope(rb.byteStream(), FxContract::class.java)
        }
    }

    override suspend fun declineContract(uuid: String, request: FxContractDeclineRequest, options: RequestOptions?): FxContract {
        val base = effectiveBaseUrl(options)
        val url = "$base/api/v1/fx-houses/contracts/$uuid/decline"
        val payload = json.writeValueAsString(request)
        val req = Request.Builder()
            .url(url)
            .patch(payload.toRequestBody(jsonMedia))
            .header("User-Agent", config.userAgent)
            .applyAuth(options)
            .applyReadOverride(options)
            .build()
        val body = executeWithRetries(req, isIdempotent = false)
        body.use { rb ->
            return JsonSupport.readSingleEnvelope(rb.byteStream(), FxContract::class.java)
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
                        resp.closeQuietly()
                        throw IOException("Empty response body")
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
                    val delayMs = computeBackoff(attempt, null)
                    delay(delayMs)
                    continue
                }
                throw io
            }
        }
    }

    private fun shouldRetry(status: Int): Boolean = when (status) {
        HttpURLConnection.HTTP_CLIENT_TIMEOUT,
        429,
        HttpURLConnection.HTTP_UNAVAILABLE,
        HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> true
        else -> false
    }

    private fun computeBackoff(attempt: Int, retryAfterSeconds: Long?): Long {
        val base = if (retryAfterSeconds != null) Duration.ofSeconds(retryAfterSeconds).toMillis() else 250L
        val exp = (1L shl attempt).coerceAtMost(64)
        val maxDelay = (base * exp).coerceAtMost(config.maxRetryDelayMillis)
        val jitter = ThreadLocalRandom.current().nextLong(maxDelay / 2, maxDelay + 1)
        return jitter
    }

    private fun toHttpException(resp: Response): FcmsHttpException {
        val status = resp.code
        val headers = resp.headers.toMultimap().mapValues { it.value.joinToString(",") }
        val bodyStr = try {
            resp.body?.string()
        } catch (_: Exception) {
            null
        }
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
