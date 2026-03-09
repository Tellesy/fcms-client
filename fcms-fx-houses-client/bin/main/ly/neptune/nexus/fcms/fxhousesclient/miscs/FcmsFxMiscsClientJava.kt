package ly.neptune.nexus.fcms.fxhousesclient.miscs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.miscs.internal.FcmsFxMiscsClientImpl
import ly.neptune.nexus.fcms.fxhousesclient.model.CodeName
import ly.neptune.nexus.fcms.fxhousesclient.model.ExchangeRate
import java.util.concurrent.CompletableFuture

class FcmsFxMiscsClientJava private constructor(
    private val delegate: FcmsFxMiscsClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun listExchangeRates(dateFilter: String?, options: RequestOptions?): CompletableFuture<List<ExchangeRate>> =
        scope.future { delegate.listExchangeRates(dateFilter, options) }

    fun listDeclineReasons(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listDeclineReasons(options) }

    fun listPurchaseRequestStates(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listPurchaseRequestStates(options) }

    fun listPurchaseRequestTypes(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listPurchaseRequestTypes(options) }

    override fun close() {
        scope.cancel("FcmsFxMiscsClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsFxConfig): FcmsFxMiscsClientJava {
            val impl: FcmsFxMiscsClient = FcmsFxMiscsClientImpl(config)
            return FcmsFxMiscsClientJava(impl)
        }
    }
}
