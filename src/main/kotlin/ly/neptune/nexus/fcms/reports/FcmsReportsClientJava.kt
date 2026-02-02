package ly.neptune.nexus.fcms.reports

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.reports.internal.FcmsReportsClientImpl
import ly.neptune.nexus.fcms.reports.model.PurchaseRequestsStatesReportRow
import java.util.concurrent.CompletableFuture

class FcmsReportsClientJava private constructor(
    private val delegate: FcmsReportsClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun purchaseRequestsStatesSummary(
        approvedOn: String?,
        type: String?,
        options: RequestOptions?,
    ): CompletableFuture<List<PurchaseRequestsStatesReportRow>> =
        scope.future { delegate.purchaseRequestsStatesSummary(approvedOn, type, options) }

    override fun close() {
        scope.cancel("FcmsReportsClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsConfig): FcmsReportsClientJava {
            val impl: FcmsReportsClient = FcmsReportsClientImpl(config)
            return FcmsReportsClientJava(impl)
        }
    }
}
