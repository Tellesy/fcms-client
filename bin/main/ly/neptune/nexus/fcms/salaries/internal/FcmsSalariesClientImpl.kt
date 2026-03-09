@file:Suppress("TooManyFunctions", "ThrowsCount", "LoopWithTooManyJumpStatements", "MagicNumber", "ReturnCount", "TooGenericExceptionCaught", "SwallowedException", "CyclomaticComplexMethod", "NestedBlockDepth", "MaxLineLength")
package ly.neptune.nexus.fcms.salaries.internal

import com.fasterxml.jackson.databind.node.ObjectNode
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.salaries.FcmsSalariesClient
import ly.neptune.nexus.fcms.salaries.HealthCheckResult
import ly.neptune.nexus.fcms.salaries.SalariesListFilter
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.core.http.FcmsHttpException
import ly.neptune.nexus.fcms.core.http.JsonSupport
import ly.neptune.nexus.fcms.core.http.OkHttpProvider
import ly.neptune.nexus.fcms.salaries.model.Page
import ly.neptune.nexus.fcms.salaries.model.RejectionReason
import ly.neptune.nexus.fcms.salaries.model.Transaction
import ly.neptune.nexus.fcms.salaries.model.request.BulkCompleteTransactionRequest
import ly.neptune.nexus.fcms.salaries.model.request.CompleteTransactionRequest
import ly.neptune.nexus.fcms.salaries.model.response.BulkCompleteTransactionResponse
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ThreadLocalRandom

internal class FcmsSalariesClientImpl(
    private val config: FcmsConfig,
) : FcmsSalariesClient {

    private val log = LoggerFactory.getLogger(FcmsSalariesClientImpl::class.java)
    private val managed = OkHttpProvider.createManaged(config)
    private val client = managed.client
    private val json = JsonSupport.mapper
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    override suspend fun listTransactions(page: Int?, options: RequestOptions?): Page<Transaction> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/mof/transactions")
            if (page != null) {
                append("?page=").append(page)
                append("&limit=500")
            } else {
                append("?limit=500")
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
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), Transaction::class.java)
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

    override suspend fun listTransactionsFiltered(
        page: Int?,
        filter: SalariesListFilter?,
        options: RequestOptions?
    ): Page<Transaction> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/mof/transactions")
            var first = true
            fun addParam(k: String, v: String?) {
                if (v.isNullOrBlank()) return
                append(if (first) "?" else "&")
                first = false
                append(k).append("=").append(v)
            }
            if (page != null) addParam("page", page.toString())
            if (filter != null) {
                addParam("filter[state]", filter.state)
                addParam("filter[salary_year]", filter.year?.toString())
                addParam("filter[salary_month]", filter.month?.toString())
            }
            addParam("limit", "500")
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
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), Transaction::class.java)
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

    override suspend fun listTransactionsWithFilters(
        page: Int?,
        filters: Map<String, String>,
        options: RequestOptions?
    ): Page<Transaction> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/mof/transactions")
            var first = true
            fun addParam(k: String, v: String?) {
                if (v.isNullOrBlank()) return
                append(if (first) "?" else "&")
                first = false
                append(k).append("=").append(v)
            }
            if (page != null) addParam("page", page.toString())
            for ((k, v) in filters) addParam(k, v)
            addParam("limit", "500")
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
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), Transaction::class.java)
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

    override suspend fun showTransaction(uuid: String, options: RequestOptions?): Transaction {
        val base = effectiveBaseUrl(options)
        val url = "$base/api/v1/mof/transactions/$uuid"
        val req = Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", config.userAgent)
            .applyAuth(options)
            .applyReadOverride(options)
            .build()
        val body = executeWithRetries(req, isIdempotent = true)
        body.use { rb ->
            return JsonSupport.readSingleEnvelope(rb.byteStream(), Transaction::class.java)
        }
    }

    override suspend fun completeTransaction(
        uuid: String,
        request: CompleteTransactionRequest,
        options: RequestOptions?
    ): Transaction {
        val base = effectiveBaseUrl(options)
        val url = "$base/api/v1/mof/transactions/$uuid/complete"
        val payload = json.writeValueAsString(request)
        val requestBody = payload.toRequestBody(jsonMedia)
        val req = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("User-Agent", config.userAgent)
            .header("Content-Type", "application/json")
            .applyAuth(options)
            .applyReadOverride(options)
            .build()
        // Log full request details for debugging
        val authHeader = req.header("Authorization")
        val maskedAuth = if (authHeader != null && authHeader.length > 15) {
            authHeader.substring(0, 15) + "..." + authHeader.takeLast(4)
        } else authHeader
        val contentType = req.header("Content-Type") ?: requestBody.contentType()?.toString()
        log.info("FCMS completeTransaction REQUEST: method={}, url={}, contentType={}, auth={}, body={}", 
            req.method, req.url, contentType, maskedAuth, payload)
        
        try {
            val body = executeWithRetries(req, isIdempotent = false)
            body.use { rb ->
                return JsonSupport.readSingleEnvelope(rb.byteStream(), Transaction::class.java)
            }
        } catch (ex: FcmsHttpException) {
            // FCMS028 = "already completed" - but FCMS sometimes lies!
            // Verify actual state before propagating error
            if (ex.status == 403 && ex.code == "FCMS028") {
                log.warn("FCMS028 received for uuid={}, verifying actual state...", uuid)
                try {
                    val tx = showTransaction(uuid, options)
                    if (tx.state == "completed") {
                        log.info("FCMS028 verified: uuid={} is actually completed", uuid)
                        return tx
                    } else {
                        log.error("FCMS028 LIE DETECTED: uuid={} state is '{}' not 'completed'! " +
                            "FCMS returned false 'already completed' error", uuid, tx.state)
                        // Re-throw with additional context
                        throw FcmsHttpException(
                            status = ex.status,
                            code = ex.code,
                            message = "FCMS028 but state=${tx.state} (FCMS bug - transaction NOT completed)",
                            body = ex.body,
                            headers = ex.headers,
                            retryAfterSeconds = ex.retryAfterSeconds
                        )
                    }
                } catch (verifyEx: Exception) {
                    log.error("Failed to verify transaction state after FCMS028: {}", verifyEx.message)
                    throw ex // Re-throw original
                }
            }
            throw ex
        }
    }

    override suspend fun rejectTransaction(
        uuid: String,
        rejectionReason: String,
        options: RequestOptions?
    ): Transaction {
        val base = effectiveBaseUrl(options)
        val url = "$base/api/v1/mof/transactions/$uuid/reject"
        val node: ObjectNode = json.createObjectNode()
        node.put("rejection_reason", rejectionReason)
        val payload = json.writeValueAsString(node)
        log.debug("FCMS rejectTransaction: uuid={}, reason={}", uuid, rejectionReason)
        val req = Request.Builder()
            .url(url)
            .post(payload.toRequestBody(jsonMedia))
            .header("User-Agent", config.userAgent)
            .applyAuth(options)
            .applyReadOverride(options)
            .build()
        val body = executeWithRetries(req, isIdempotent = false)
        body.use { rb ->
            return JsonSupport.readSingleEnvelope(rb.byteStream(), Transaction::class.java)
        }
    }

    override suspend fun listRejectionReasons(options: RequestOptions?): List<RejectionReason> {
        val base = effectiveBaseUrl(options)
        val url = "$base/api/v1/misc/mof/rejection-reasons"
        val req = Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", config.userAgent)
            .applyAuth(options)
            .applyReadOverride(options)
            .build()
        val body = executeWithRetries(req, isIdempotent = true)
        body.use { rb ->
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), RejectionReason::class.java)
            return pr.data
        }
    }

    override suspend fun healthCheck(options: RequestOptions?): HealthCheckResult {
        val base = effectiveBaseUrl(options)
        val url = "$base/api/v1/misc/mof/rejection-reasons"
        val req = Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", config.userAgent)
            .applyAuth(options)
            .build()
        
        val startTime = System.currentTimeMillis()
        return try {
            val call = client.newCall(req)
            val resp = call.execute()
            val latency = System.currentTimeMillis() - startTime
            
            resp.use { response ->
                when {
                    response.isSuccessful -> {
                        log.info("FCMS Health Check: OK ({} ms)", latency)
                        HealthCheckResult.success(latency)
                    }
                    response.code in 401..403 -> {
                        val msg = tryExtractErrorMessage(response)
                        log.warn("FCMS Health Check: Authentication failed - {} ({} ms)", response.code, latency)
                        HealthCheckResult.authenticationError(latency, response.code, msg)
                    }
                    else -> {
                        val msg = tryExtractErrorMessage(response)
                        log.warn("FCMS Health Check: Server error - {} ({} ms)", response.code, latency)
                        HealthCheckResult.serverError(latency, response.code, msg)
                    }
                }
            }
        } catch (e: IOException) {
            val latency = System.currentTimeMillis() - startTime
            log.error("FCMS Health Check: Connection failed - {} ({} ms)", e.message, latency)
            HealthCheckResult.connectionError(latency, e.message ?: "Connection failed")
        }
    }

    private fun tryExtractErrorMessage(response: Response): String? {
        return try {
            val bodyStr = response.body?.string()
            if (!bodyStr.isNullOrBlank()) {
                val node = json.readTree(bodyStr)
                // FCMS returns errors in nested structure: {"error":{"code":"...","message":"..."}}
                val errorNode = node.get("error")
                if (errorNode != null && errorNode.isObject) {
                    errorNode.get("message")?.asText()
                } else {
                    node.get("message")?.asText()
                }
            } else null
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun listArchivedTransactions(page: Int?, options: RequestOptions?): Page<Transaction> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/mof/archived-transactions")
            if (page != null) {
                append("?page=").append(page)
                append("&limit=500")
            } else {
                append("?limit=500")
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
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), Transaction::class.java)
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

    override suspend fun listArchivedTransactionsFiltered(
        page: Int?,
        filter: SalariesListFilter?,
        options: RequestOptions?
    ): Page<Transaction> {
        val base = effectiveBaseUrl(options)
        val url = buildString {
            append(base)
            append("/api/v1/mof/archived-transactions")
            var first = true
            fun addParam(k: String, v: String?) {
                if (v.isNullOrBlank()) return
                append(if (first) "?" else "&")
                first = false
                append(k).append("=").append(v)
            }
            if (page != null) addParam("page", page.toString())
            if (filter != null) {
                addParam("filter[state]", filter.state)
                addParam("filter[salary_year]", filter.year?.toString())
                addParam("filter[salary_month]", filter.month?.toString())
            }
            addParam("limit", "500")
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
            val pr = JsonSupport.readListEnvelope(rb.byteStream(), Transaction::class.java)
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

    override suspend fun bulkCompleteTransactions(
        request: BulkCompleteTransactionRequest,
        options: RequestOptions?
    ): BulkCompleteTransactionResponse {
        val base = effectiveBaseUrl(options)
        val url = "$base/api/v1/mof/transactions/bulk-complete"
        val payload = json.writeValueAsString(request)
        val req = Request.Builder()
            .url(url)
            .post(payload.toRequestBody(jsonMedia))
            .header("User-Agent", config.userAgent)
            .applyAuth(options)
            .applyReadOverride(options)
            .build()
        // Log full request details for debugging
        val authHeader = req.header("Authorization")
        val maskedAuth = if (authHeader != null && authHeader.length > 15) {
            authHeader.substring(0, 15) + "..." + authHeader.takeLast(4)
        } else authHeader
        log.info("FCMS bulkCompleteTransactions REQUEST: method={}, url={}, auth={}, count={}", 
            req.method, req.url, maskedAuth, request.transactions.size)
        log.debug("FCMS bulkCompleteTransactions payload: {}", payload)
        val body = executeWithRetries(req, isIdempotent = false)
        body.use { rb ->
            return JsonSupport.readSingleEnvelope(rb.byteStream(), BulkCompleteTransactionResponse::class.java)
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
        log.debug("FCMS Request: {} {}", req.method, req.url)
        var attempt = 0
        // For HTTP status retries, only retry idempotent requests
        val enableHttpRetry = config.enableRetries && isIdempotent
        // For IO errors (connection reset, timeout), always retry if retries enabled
        // because the request may not have reached the server
        val enableIoRetry = config.enableRetries
        val max = if (config.enableRetries) config.maxRetries.coerceAtLeast(0) else 0
        while (true) {
            try {
                val call = client.newCall(req)
                val startTime = System.currentTimeMillis()
                val resp = call.execute()
                val duration = System.currentTimeMillis() - startTime
                
                if (resp.isSuccessful) {
                    log.info("FCMS Response: {} {} -> {} ({} ms)", req.method, req.url, resp.code, duration)
                    return resp.body ?: run {
                        resp.closeQuietly()
                        throw IOException("Empty response body")
                    }
                }
                // Non-2xx
                val ex = toHttpException(resp)
                log.warn("FCMS Error Response: {} {} -> {} ({} ms) - {}", 
                    req.method, req.url, resp.code, duration, ex.message ?: ex.body)
                resp.closeQuietly()
                if (enableHttpRetry && shouldRetry(ex.status)) {
                    val delayMs = computeBackoff(attempt, ex.retryAfterSeconds)
                    if (attempt < max) {
                        attempt++
                        log.info("FCMS Retry: attempt {} of {} for {} {} (delay {} ms)", 
                            attempt, max, req.method, req.url, delayMs)
                        delay(delayMs)
                        continue
                    }
                }
                throw ex
            } catch (io: IOException) {
                log.error("FCMS IO Error: {} {} - {}", req.method, req.url, io.message)
                // Evict stale connections on IO errors (timeout, socket closed, etc.)
                client.connectionPool.evictAll()
                log.debug("FCMS: Evicted connection pool after IO error")
                // Always retry IO errors if retries enabled - request may not have reached server
                if (enableIoRetry && attempt < max) {
                    attempt++
                    val delayMs = computeBackoff(attempt, null)
                    log.info("FCMS Retry after IO error: attempt {} of {} (delay {} ms)", attempt, max, delayMs)
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
        log.debug("FCMS Error Details: status={}, url={}, body={}", status, resp.request.url, bodyStr)
        var code: String? = null
        var message: String? = null
        if (!bodyStr.isNullOrBlank()) {
            try {
                val node = json.readTree(bodyStr)
                // FCMS returns errors in nested structure: {"error":{"code":"FCMS028","message":"..."}}
                val errorNode = node.get("error")
                if (errorNode != null && errorNode.isObject) {
                    code = errorNode.get("code")?.asText()
                    message = errorNode.get("message")?.asText()
                }
                // Fallback to root level if not in nested structure
                if (code == null) code = node.get("code")?.asText()
                if (message == null) message = node.get("message")?.asText()
                log.debug("FCMS Error Parsed: code={}, message={}", code, message)
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
        managed.close()
    }
}
