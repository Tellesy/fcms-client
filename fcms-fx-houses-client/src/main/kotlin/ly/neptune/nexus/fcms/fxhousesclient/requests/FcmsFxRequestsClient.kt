package ly.neptune.nexus.fcms.fxhousesclient.requests

import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.core.model.Page
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.DeletedPurchaseRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestActionRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestDeclineRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestProcessRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestQueueItem
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestsListFilter
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.RefreshQueueResult

interface FcmsFxRequestsClient : AutoCloseable {
    suspend fun listPendingRequests(page: Int? = null, options: RequestOptions? = null): Page<PurchaseRequestQueueItem>

    suspend fun listPurchaseRequests(
        page: Int? = null,
        filter: PurchaseRequestsListFilter? = null,
        options: RequestOptions? = null,
    ): Page<PurchaseRequest>

    suspend fun showPurchaseRequest(uuid: String, options: RequestOptions? = null): PurchaseRequest

    suspend fun approvePurchaseRequest(
        uuid: String,
        request: PurchaseRequestActionRequest,
        options: RequestOptions? = null,
    ): PurchaseRequest

    suspend fun declinePurchaseRequest(
        uuid: String,
        request: PurchaseRequestDeclineRequest,
        options: RequestOptions? = null,
    ): PurchaseRequest

    suspend fun processPurchaseRequest(
        uuid: String,
        request: PurchaseRequestProcessRequest,
        options: RequestOptions? = null,
    ): PurchaseRequest

    suspend fun listDeletedPurchaseRequests(
        page: Int? = null,
        uuidFilter: String? = null,
        ibanFilter: String? = null,
        options: RequestOptions? = null,
    ): Page<DeletedPurchaseRequest>

    suspend fun refreshQueue(options: RequestOptions? = null): RefreshQueueResult
}
