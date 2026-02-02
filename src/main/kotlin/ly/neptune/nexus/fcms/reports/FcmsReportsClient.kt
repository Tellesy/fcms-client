package ly.neptune.nexus.fcms.reports

import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.reports.model.PurchaseRequestsStatesReportRow

interface FcmsReportsClient : AutoCloseable {
    suspend fun purchaseRequestsStatesSummary(
        approvedOn: String? = null,
        type: String? = null,
        options: RequestOptions? = null,
    ): List<PurchaseRequestsStatesReportRow>
}
