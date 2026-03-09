package ly.neptune.nexus.fcms.fxhouses

import ly.neptune.nexus.fcms.accounts.model.BankAccount
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.fxhouses.model.FxContract
import ly.neptune.nexus.fcms.fxhouses.model.FxContractsListFilter
import ly.neptune.nexus.fcms.fxhouses.model.FxHouse
import ly.neptune.nexus.fcms.fxhouses.model.FxPurchaseRequest
import ly.neptune.nexus.fcms.fxhouses.model.FxPurchaseRequestsListFilter
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractActionRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractDeclineRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractProcessRequest
import ly.neptune.nexus.fcms.salaries.model.Page

interface FcmsFxHousesClient : AutoCloseable {
    suspend fun listBankAccounts(page: Int? = null, options: RequestOptions? = null): Page<BankAccount>

    suspend fun listFxHouses(page: Int? = null, options: RequestOptions? = null): Page<FxHouse>

    suspend fun listContracts(
        page: Int? = null,
        filter: FxContractsListFilter? = null,
        options: RequestOptions? = null,
    ): Page<FxContract>

    suspend fun listFxPurchaseRequests(
        page: Int? = null,
        filter: FxPurchaseRequestsListFilter? = null,
        options: RequestOptions? = null,
    ): Page<FxPurchaseRequest>

    suspend fun approveContract(
        uuid: String,
        request: FxContractActionRequest,
        options: RequestOptions? = null
    ): FxContract

    suspend fun processContract(
        uuid: String,
        request: FxContractProcessRequest,
        options: RequestOptions? = null
    ): FxContract

    suspend fun declineContract(
        uuid: String,
        request: FxContractDeclineRequest,
        options: RequestOptions? = null
    ): FxContract
}
