package ly.neptune.nexus.fcms.fxhousesclient.contracts

import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContract
import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContractCreateRequest
import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.core.model.Page

interface FcmsFxContractsClient : AutoCloseable {
    suspend fun create(
        request: FxContractCreateRequest,
        options: RequestOptions? = null,
    ): FxContract

    suspend fun list(
        page: Int? = null,
        options: RequestOptions? = null,
    ): Page<FxContract>
}
