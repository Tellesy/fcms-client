package ly.neptune.nexus.fcms.salaries

import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.salaries.model.Transaction
import ly.neptune.nexus.fcms.salaries.model.Page
import ly.neptune.nexus.fcms.salaries.model.RejectionReason
import ly.neptune.nexus.fcms.salaries.model.request.BulkCompleteTransactionRequest
import ly.neptune.nexus.fcms.salaries.model.request.CompleteTransactionRequest
import ly.neptune.nexus.fcms.salaries.model.response.BulkCompleteTransactionResponse

/**
 * Main FCMS Salaries client interface.
 *
 * Kotlin-first suspend API. Use [FcmsSalariesClientJava] for Java-friendly CompletableFuture facade.
 */
@Suppress("TooManyFunctions")
interface FcmsSalariesClient : AutoCloseable {
    /** List transactions with optional page parameter. */
    suspend fun listTransactions(page: Int? = null, options: RequestOptions? = null): Page<Transaction>

    /** List transactions with optional page and typed filters. */
    suspend fun listTransactionsFiltered(
        page: Int? = null,
        filter: SalariesListFilter? = null,
        options: RequestOptions? = null
    ): Page<Transaction>

    /** Manually provide raw query filters, e.g. mapOf("filter[state]" to "pending"). */
    suspend fun listTransactionsWithFilters(
        page: Int? = null,
        filters: Map<String, String>,
        options: RequestOptions? = null
    ): Page<Transaction>

    /** Fetch a single transaction by UUID. */
    suspend fun showTransaction(uuid: String, options: RequestOptions? = null): Transaction

    /** Mark a transaction as completed with bank reference and timestamp. */
    suspend fun completeTransaction(
        uuid: String,
        request: CompleteTransactionRequest,
        options: RequestOptions? = null
    ): Transaction

    /** Reject a transaction with a rejection reason code. */
    suspend fun rejectTransaction(
        uuid: String,
        rejectionReason: String,
        options: RequestOptions? = null
    ): Transaction

    /** List supported rejection reasons. */
    suspend fun listRejectionReasons(options: RequestOptions? = null): List<RejectionReason>

    /** 
     * Health check to verify connectivity and authentication with FCMS.
     * Returns a [HealthCheckResult] with status, latency, and any error details.
     */
    suspend fun healthCheck(options: RequestOptions? = null): HealthCheckResult

    /** List archived transactions with optional page parameter. */
    suspend fun listArchivedTransactions(page: Int? = null, options: RequestOptions? = null): Page<Transaction>

    /** List archived transactions with optional page and typed filters. */
    suspend fun listArchivedTransactionsFiltered(
        page: Int? = null,
        filter: SalariesListFilter? = null,
        options: RequestOptions? = null
    ): Page<Transaction>

    /** Bulk complete multiple transactions at once. */
    suspend fun bulkCompleteTransactions(
        request: BulkCompleteTransactionRequest,
        options: RequestOptions? = null
    ): BulkCompleteTransactionResponse

    // Convenience helpers
    /** list with filter[state]=<state> */
    suspend fun listTransactionsByState(
        state: String,
        page: Int? = null,
        options: RequestOptions? = null
    ): Page<Transaction> = listTransactionsFiltered(page, SalariesListFilter(state = state), options)

    /** list with filter[year]=<year> */
    suspend fun listTransactionsByYear(
        year: Int,
        page: Int? = null,
        options: RequestOptions? = null
    ): Page<Transaction> = listTransactionsFiltered(page, SalariesListFilter(year = year), options)

    /** list with filter[year]=<year>&filter[month]=<month> */
    suspend fun listTransactionsByYearMonth(
        year: Int,
        month: Int,
        page: Int? = null,
        options: RequestOptions? = null
    ): Page<Transaction> = listTransactionsFiltered(page, SalariesListFilter(year = year, month = month), options)

    /** list with filter[year], filter[month], filter[state] */
    suspend fun listTransactionsByYearMonthState(
        year: Int,
        month: Int,
        state: String,
        page: Int? = null,
        options: RequestOptions? = null
    ): Page<Transaction> =
        listTransactionsFiltered(
            page,
            SalariesListFilter(state = state, year = year, month = month),
            options,
        )
}

/** Query filters for listing salary transactions. */
data class SalariesListFilter(
    val state: String? = null,
    val year: Int? = null,
    val month: Int? = null,
)

/**
 * Result of a health check against FCMS.
 *
 * @property healthy True if the connection and authentication succeeded.
 * @property latencyMs Round-trip latency in milliseconds.
 * @property statusCode HTTP status code (null if connection failed before HTTP response).
 * @property errorMessage Error message if unhealthy.
 * @property errorType Type of error: "connection", "authentication", "server", or null if healthy.
 */
data class HealthCheckResult(
    val healthy: Boolean,
    val latencyMs: Long,
    val statusCode: Int? = null,
    val errorMessage: String? = null,
    val errorType: String? = null,
) {
    companion object {
        fun success(latencyMs: Long): HealthCheckResult =
            HealthCheckResult(healthy = true, latencyMs = latencyMs, statusCode = 200)

        fun connectionError(latencyMs: Long, message: String): HealthCheckResult =
            HealthCheckResult(
                healthy = false,
                latencyMs = latencyMs,
                errorMessage = message,
                errorType = "connection"
            )

        fun authenticationError(latencyMs: Long, statusCode: Int, message: String?): HealthCheckResult =
            HealthCheckResult(
                healthy = false,
                latencyMs = latencyMs,
                statusCode = statusCode,
                errorMessage = message ?: "Authentication failed",
                errorType = "authentication"
            )

        fun serverError(latencyMs: Long, statusCode: Int, message: String?): HealthCheckResult =
            HealthCheckResult(
                healthy = false,
                latencyMs = latencyMs,
                statusCode = statusCode,
                errorMessage = message ?: "Server error",
                errorType = "server"
            )
    }
}
