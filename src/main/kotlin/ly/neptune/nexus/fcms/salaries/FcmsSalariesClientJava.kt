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
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

/**
 * Java-friendly CompletableFuture facade for [FcmsSalariesClient].
 */
@Suppress("TooManyFunctions")
class FcmsSalariesClientJava private constructor(
    private val delegate: FcmsSalariesClient,
) : AutoCloseable {

    private val scope = CoroutineScope(Dispatchers.IO)

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

    override fun close() {
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
