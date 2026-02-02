package ly.neptune.nexus.fcms.fxhousesclient.exchangerates

import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.core.model.Page
import ly.neptune.nexus.fcms.fxhousesclient.model.ExchangeRate

interface FcmsFxExchangeRatesClient : AutoCloseable {
    suspend fun list(page: Int? = null, options: RequestOptions? = null): Page<ExchangeRate>
}
