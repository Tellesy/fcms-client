package ly.neptune.nexus.fcms.requests

import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.requests.model.DeletedPurchaseRequest
import ly.neptune.nexus.fcms.requests.model.PurchaseRequest
import ly.neptune.nexus.fcms.requests.model.PurchaseRequestActionRequest
import ly.neptune.nexus.fcms.requests.model.PurchaseRequestDeclineRequest
import ly.neptune.nexus.fcms.requests.model.PurchaseRequestProcessRequest
import ly.neptune.nexus.fcms.requests.model.PurchaseRequestsListFilter
import ly.neptune.nexus.fcms.requests.model.PurchaseRequestQueueItem
import ly.neptune.nexus.fcms.requests.model.RefreshQueueResult
import ly.neptune.nexus.fcms.salaries.model.Page

/**
 * FCMS Requests client interface for listing pending purchase requests queue.
 */
interface FcmsRequestsClient : AutoCloseable {
    /** List pending purchase requests on the queue. */
    suspend fun listPendingRequests(
        page: Int? = null,
        options: RequestOptions? = null
    ): Page<PurchaseRequestQueueItem>

    suspend fun listPurchaseRequests(
        page: Int? = null,
        filter: PurchaseRequestsListFilter? = null,
        options: RequestOptions? = null,
    ): Page<PurchaseRequest>

    suspend fun showPurchaseRequest(
        uuid: String,
        options: RequestOptions? = null,
    ): PurchaseRequest

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
