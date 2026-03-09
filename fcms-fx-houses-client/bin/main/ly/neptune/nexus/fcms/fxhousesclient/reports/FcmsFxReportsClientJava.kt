package ly.neptune.nexus.fcms.fxhousesclient.reports

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.reports.internal.FcmsFxReportsClientImpl
import ly.neptune.nexus.fcms.fxhousesclient.reports.model.PurchaseRequestsStatesReport
import java.util.concurrent.CompletableFuture

class FcmsFxReportsClientJava private constructor(
    private val delegate: FcmsFxReportsClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun purchaseRequestsStatesSummary(
        approvedOn: String?,
        type: String?,
        options: RequestOptions?,
    ): CompletableFuture<PurchaseRequestsStatesReport> =
        scope.future { delegate.purchaseRequestsStatesSummary(approvedOn, type, options) }

    override fun close() {
        scope.cancel("FcmsFxReportsClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsFxConfig): FcmsFxReportsClientJava {
            val impl: FcmsFxReportsClient = FcmsFxReportsClientImpl(config)
            return FcmsFxReportsClientJava(impl)
        }
    }
}
