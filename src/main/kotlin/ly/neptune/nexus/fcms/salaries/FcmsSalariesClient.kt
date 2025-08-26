package ly.neptune.nexus.fcms.salaries

import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.salaries.model.Transaction
import ly.neptune.nexus.fcms.salaries.model.Page
import ly.neptune.nexus.fcms.salaries.model.RejectionReason
import ly.neptune.nexus.fcms.salaries.model.request.CompleteTransactionRequest

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
