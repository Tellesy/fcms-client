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
interface FcmsSalariesClient : AutoCloseable {
    /** List transactions with optional page parameter. */
    suspend fun listTransactions(page: Int? = null, options: RequestOptions? = null): Page<Transaction>

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
}
