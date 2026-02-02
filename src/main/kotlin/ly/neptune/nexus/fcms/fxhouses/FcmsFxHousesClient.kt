package ly.neptune.nexus.fcms.fxhouses

import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.fxhouses.model.FxContract
import ly.neptune.nexus.fcms.fxhouses.model.FxHouse
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractActionRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractDeclineRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractProcessRequest
import ly.neptune.nexus.fcms.salaries.model.Page

interface FcmsFxHousesClient : AutoCloseable {
    suspend fun listFxHouses(page: Int? = null, options: RequestOptions? = null): Page<FxHouse>

    suspend fun listContracts(page: Int? = null, options: RequestOptions? = null): Page<FxContract>

    suspend fun approveContract(uuid: String, request: FxContractActionRequest, options: RequestOptions? = null): FxContract

    suspend fun processContract(uuid: String, request: FxContractProcessRequest, options: RequestOptions? = null): FxContract

    suspend fun declineContract(uuid: String, request: FxContractDeclineRequest, options: RequestOptions? = null): FxContract
}
