package ly.neptune.nexus.fcms.salaries

import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.salaries.internal.FcmsSalariesClientImpl
import ly.neptune.nexus.fcms.salaries.model.Page
import ly.neptune.nexus.fcms.salaries.model.Transaction
import ly.neptune.nexus.fcms.salaries.model.RejectionReason
import ly.neptune.nexus.fcms.salaries.model.request.CompleteTransactionRequest

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

/**
 * Java-friendly CompletableFuture facade for [FcmsSalariesClient].
 */
class FcmsSalariesClientJava private constructor(
    private val delegate: FcmsSalariesClient,
) : AutoCloseable {

    private val scope = CoroutineScope(Dispatchers.IO)

    fun listTransactions(
        page: Int?,
        options: RequestOptions?
    ): CompletableFuture<Page<Transaction>> =
        scope.future { delegate.listTransactions(page, options) }

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
