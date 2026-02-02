package ly.neptune.nexus.fcms.fxhousesclient.exchangerates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.core.model.Page
import ly.neptune.nexus.fcms.fxhousesclient.exchangerates.internal.FcmsFxExchangeRatesClientImpl
import ly.neptune.nexus.fcms.fxhousesclient.model.ExchangeRate
import java.util.concurrent.CompletableFuture

class FcmsFxExchangeRatesClientJava private constructor(
    private val delegate: FcmsFxExchangeRatesClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun list(page: Int?, options: RequestOptions?): CompletableFuture<Page<ExchangeRate>> =
        scope.future { delegate.list(page, options) }

    override fun close() {
        scope.cancel("FcmsFxExchangeRatesClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsFxConfig): FcmsFxExchangeRatesClientJava {
            val impl: FcmsFxExchangeRatesClient = FcmsFxExchangeRatesClientImpl(config)
            return FcmsFxExchangeRatesClientJava(impl)
        }
    }
}
