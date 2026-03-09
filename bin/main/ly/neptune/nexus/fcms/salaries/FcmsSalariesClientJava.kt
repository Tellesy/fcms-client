package ly.neptune.nexus.fcms.salaries

import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.salaries.internal.FcmsSalariesClientImpl
import ly.neptune.nexus.fcms.salaries.SalariesListFilter
import ly.neptune.nexus.fcms.salaries.model.Page
import ly.neptune.nexus.fcms.salaries.model.Transaction
import ly.neptune.nexus.fcms.salaries.model.RejectionReason
import ly.neptune.nexus.fcms.salaries.model.request.BulkCompleteTransactionRequest
import ly.neptune.nexus.fcms.salaries.model.request.CompleteTransactionRequest
import ly.neptune.nexus.fcms.salaries.model.response.BulkCompleteTransactionResponse

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

/**
 * Java-friendly CompletableFuture facade for [FcmsSalariesClient].
 */
@Suppress("TooManyFunctions")
class FcmsSalariesClientJava private constructor(
    private val delegate: FcmsSalariesClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun listTransactions(
        page: Int?,
        options: RequestOptions?
    ): CompletableFuture<Page<Transaction>> =
        scope.future { delegate.listTransactions(page, options) }

    fun listTransactions(
        page: Int?,
        filter: SalariesListFilter?,
        options: RequestOptions?
    ): CompletableFuture<Page<Transaction>> =
        scope.future { delegate.listTransactionsFiltered(page, filter, options) }

    fun listTransactionsWithFilters(
        page: Int?,
        filters: Map<String, String>,
        options: RequestOptions?
    ): CompletableFuture<Page<Transaction>> =
        scope.future { delegate.listTransactionsWithFilters(page, filters, options) }

    fun showTransaction(
        uuid: String,
        options: RequestOptions?
    ): CompletableFuture<Transaction> =
        scope.future { delegate.showTransaction(uuid, options) }

    fun completeTransaction(
        uuid: String,
        req: CompleteTransactionRequest,
        options: RequestOptions?
    ): CompletableFuture<Transaction> =
        scope.future { delegate.completeTransaction(uuid, req, options) }

    fun rejectTransaction(
        uuid: String,
        rejectionReason: String,
        options: RequestOptions?
    ): CompletableFuture<Transaction> =
        scope.future { delegate.rejectTransaction(uuid, rejectionReason, options) }

    fun listRejectionReasons(options: RequestOptions?): CompletableFuture<List<RejectionReason>> =
        scope.future { delegate.listRejectionReasons(options) }

    /**
     * Health check to verify connectivity and authentication with FCMS.
     * Returns a [HealthCheckResult] with status, latency, and any error details.
     */
    fun healthCheck(options: RequestOptions?): CompletableFuture<HealthCheckResult> =
        scope.future { delegate.healthCheck(options) }

    fun listArchivedTransactions(
        page: Int?,
        options: RequestOptions?
    ): CompletableFuture<Page<Transaction>> =
        scope.future { delegate.listArchivedTransactions(page, options) }

    fun listArchivedTransactions(
        page: Int?,
        filter: SalariesListFilter?,
        options: RequestOptions?
    ): CompletableFuture<Page<Transaction>> =
        scope.future { delegate.listArchivedTransactionsFiltered(page, filter, options) }

    fun bulkCompleteTransactions(
        request: BulkCompleteTransactionRequest,
        options: RequestOptions?
    ): CompletableFuture<BulkCompleteTransactionResponse> =
        scope.future { delegate.bulkCompleteTransactions(request, options) }

    // Convenience wrappers
    fun listTransactionsByState(
        state: String,
        page: Int?,
        options: RequestOptions?
    ): CompletableFuture<Page<Transaction>> =
        scope.future { delegate.listTransactionsByState(state, page, options) }

    fun listTransactionsByYear(
        year: Int,
        page: Int?,
        options: RequestOptions?
    ): CompletableFuture<Page<Transaction>> =
        scope.future { delegate.listTransactionsByYear(year, page, options) }

    fun listTransactionsByYearMonth(
        year: Int,
        month: Int,
        page: Int?,
        options: RequestOptions?
    ): CompletableFuture<Page<Transaction>> =
        scope.future { delegate.listTransactionsByYearMonth(year, month, page, options) }

    fun listTransactionsByYearMonthState(
        year: Int,
        month: Int,
        state: String,
        page: Int?,
        options: RequestOptions?
    ): CompletableFuture<Page<Transaction>> =
        scope.future { delegate.listTransactionsByYearMonthState(year, month, state, page, options) }

    /**
     * Get just the state of a transaction by UUID.
     * Useful for verifying actual transaction state after receiving error responses.
     */
    fun getTransactionState(
        uuid: String,
        options: RequestOptions?
    ): CompletableFuture<String> =
        scope.future { delegate.getTransactionState(uuid, options) }

    /**
     * Verify if a transaction is actually completed.
     * Use this after receiving FCMS028 error to confirm the transaction was really completed.
     */
    fun isTransactionCompleted(
        uuid: String,
        options: RequestOptions?
    ): CompletableFuture<Boolean> =
        scope.future { delegate.isTransactionCompleted(uuid, options) }

    /**
     * Verify transaction state and return detailed result.
     * Use this after receiving FCMS028 ("already completed") error to verify actual state.
     * 
     * Example usage:
     * ```java
     * try {
     *     fcmsClient.completeTransaction(uuid, request, null).join();
     * } catch (CompletionException e) {
     *     if (e.getCause() instanceof FcmsHttpException) {
     *         FcmsHttpException fcmsEx = (FcmsHttpException) e.getCause();
     *         if ("FCMS028".equals(fcmsEx.getCode())) {
     *             // FCMS says "already completed" - verify!
     *             TransactionVerificationResult result = 
     *                 fcmsClient.verifyTransactionState(uuid, "completed", null).join();
     *             if (result.getFcmsLied()) {
     *                 log.warn("FCMS028 but tx still {}, will retry", result.getActualState());
     *                 // Don't mark as completed, retry later
     *             } else {
     *                 // Actually completed, safe to mark as done
     *             }
     *         }
     *     }
     * }
     * ```
     */
    fun verifyTransactionState(
        uuid: String,
        expectedState: String,
        options: RequestOptions?
    ): CompletableFuture<TransactionVerificationResult> =
        scope.future { delegate.verifyTransactionState(uuid, expectedState, options) }

    override fun close() {
        scope.cancel("FcmsSalariesClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsConfig): FcmsSalariesClientJava {
            val impl: FcmsSalariesClient = FcmsSalariesClientImpl(config)
            return FcmsSalariesClientJava(impl)
        }
    }
}
