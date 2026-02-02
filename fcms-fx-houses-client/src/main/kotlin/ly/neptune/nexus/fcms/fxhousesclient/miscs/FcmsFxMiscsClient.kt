package ly.neptune.nexus.fcms.fxhousesclient.miscs

import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.model.CodeName
import ly.neptune.nexus.fcms.fxhousesclient.model.ExchangeRate

interface FcmsFxMiscsClient : AutoCloseable {
    suspend fun listExchangeRates(dateFilter: String? = null, options: RequestOptions? = null): List<ExchangeRate>

    suspend fun listDeclineReasons(options: RequestOptions? = null): List<CodeName>

    suspend fun listPurchaseRequestStates(options: RequestOptions? = null): List<CodeName>

    suspend fun listPurchaseRequestTypes(options: RequestOptions? = null): List<CodeName>
}
