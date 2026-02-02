package ly.neptune.nexus.fcms.fxhousesclient.contracts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.fxhousesclient.contracts.internal.FcmsFxContractsClientImpl
import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContract
import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContractCreateRequest
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.core.model.Page
import java.util.concurrent.CompletableFuture

class FcmsFxContractsClientJava private constructor(
    private val delegate: FcmsFxContractsClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun create(request: FxContractCreateRequest, options: RequestOptions?): CompletableFuture<FxContract> =
        scope.future { delegate.create(request, options) }

    fun list(page: Int?, options: RequestOptions?): CompletableFuture<Page<FxContract>> =
        scope.future { delegate.list(page, options) }

    override fun close() {
        scope.cancel("FcmsFxContractsClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsFxConfig): FcmsFxContractsClientJava {
            val impl: FcmsFxContractsClient = FcmsFxContractsClientImpl(config)
            return FcmsFxContractsClientJava(impl)
        }
    }
}
