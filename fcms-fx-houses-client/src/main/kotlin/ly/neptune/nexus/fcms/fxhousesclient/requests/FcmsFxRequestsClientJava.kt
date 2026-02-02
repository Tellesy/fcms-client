package ly.neptune.nexus.fcms.fxhousesclient.requests

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.core.model.Page
import ly.neptune.nexus.fcms.fxhousesclient.requests.internal.FcmsFxRequestsClientImpl
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.DeletedPurchaseRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestActionRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestDeclineRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestProcessRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestQueueItem
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestsListFilter
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.RefreshQueueResult
import java.util.concurrent.CompletableFuture

class FcmsFxRequestsClientJava private constructor(
    private val delegate: FcmsFxRequestsClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun listPendingRequests(page: Int?, options: RequestOptions?): CompletableFuture<Page<PurchaseRequestQueueItem>> =
        scope.future { delegate.listPendingRequests(page, options) }

    fun listPurchaseRequests(
        page: Int?,
        filter: PurchaseRequestsListFilter?,
        options: RequestOptions?,
    ): CompletableFuture<Page<PurchaseRequest>> =
        scope.future { delegate.listPurchaseRequests(page, filter, options) }

    fun showPurchaseRequest(uuid: String, options: RequestOptions?): CompletableFuture<PurchaseRequest> =
        scope.future { delegate.showPurchaseRequest(uuid, options) }

    fun approvePurchaseRequest(
        uuid: String,
        request: PurchaseRequestActionRequest,
        options: RequestOptions?,
    ): CompletableFuture<PurchaseRequest> =
        scope.future { delegate.approvePurchaseRequest(uuid, request, options) }

    fun declinePurchaseRequest(
        uuid: String,
        request: PurchaseRequestDeclineRequest,
        options: RequestOptions?,
    ): CompletableFuture<PurchaseRequest> =
        scope.future { delegate.declinePurchaseRequest(uuid, request, options) }

    fun processPurchaseRequest(
        uuid: String,
        request: PurchaseRequestProcessRequest,
        options: RequestOptions?,
    ): CompletableFuture<PurchaseRequest> =
        scope.future { delegate.processPurchaseRequest(uuid, request, options) }

    fun listDeletedPurchaseRequests(
        page: Int?,
        uuidFilter: String?,
        ibanFilter: String?,
        options: RequestOptions?,
    ): CompletableFuture<Page<DeletedPurchaseRequest>> =
        scope.future { delegate.listDeletedPurchaseRequests(page, uuidFilter, ibanFilter, options) }

    fun refreshQueue(options: RequestOptions?): CompletableFuture<RefreshQueueResult> =
        scope.future { delegate.refreshQueue(options) }

    override fun close() {
        scope.cancel("FcmsFxRequestsClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsFxConfig): FcmsFxRequestsClientJava {
            val impl: FcmsFxRequestsClient = FcmsFxRequestsClientImpl(config)
            return FcmsFxRequestsClientJava(impl)
        }
    }
}
