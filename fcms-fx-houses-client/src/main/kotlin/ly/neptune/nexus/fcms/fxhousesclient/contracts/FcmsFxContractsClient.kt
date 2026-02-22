package ly.neptune.nexus.fcms.fxhousesclient.contracts

import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContract
import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContractActionRequest
import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContractCreateRequest
import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContractDeclineRequest
import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContractProcessRequest
import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContractsListFilter
import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.core.model.Page

interface FcmsFxContractsClient : AutoCloseable {
    suspend fun create(
        request: FxContractCreateRequest,
        options: RequestOptions? = null,
    ): FxContract

    suspend fun list(
        page: Int? = null,
        filter: FxContractsListFilter? = null,
        options: RequestOptions? = null,
    ): Page<FxContract>

    suspend fun approve(
        uuid: String,
        request: FxContractActionRequest,
        options: RequestOptions? = null,
    ): FxContract

    suspend fun process(
        uuid: String,
        request: FxContractProcessRequest,
        options: RequestOptions? = null,
    ): FxContract

    suspend fun decline(
        uuid: String,
        request: FxContractDeclineRequest,
        options: RequestOptions? = null,
    ): FxContract
}
