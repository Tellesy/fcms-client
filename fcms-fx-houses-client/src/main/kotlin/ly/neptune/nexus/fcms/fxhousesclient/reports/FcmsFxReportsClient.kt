package ly.neptune.nexus.fcms.fxhousesclient.reports

import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.reports.model.PurchaseRequestsStatesReport

interface FcmsFxReportsClient : AutoCloseable {
    suspend fun purchaseRequestsStatesSummary(
        approvedOn: String? = null,
        type: String? = null,
        options: RequestOptions? = null,
    ): PurchaseRequestsStatesReport
}
