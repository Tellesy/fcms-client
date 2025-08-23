package ly.neptune.nexus.fcms.requests

import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.requests.model.PurchaseRequestQueueItem
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
}
