package ly.neptune.nexus.fcms.fxhousesclient.exchangerates

import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.exchangerates.internal.FcmsFxExchangeRatesClientImpl

object FcmsFxExchangeRatesClients {
    @JvmStatic
    fun create(config: FcmsFxConfig): FcmsFxExchangeRatesClient = FcmsFxExchangeRatesClientImpl(config)
}
