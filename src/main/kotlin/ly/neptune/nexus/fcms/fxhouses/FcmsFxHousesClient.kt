package ly.neptune.nexus.fcms.fxhouses

import ly.neptune.nexus.fcms.accounts.model.BankAccount
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.fxhouses.model.CashContract
import ly.neptune.nexus.fcms.fxhouses.model.CashContractsListFilter
import ly.neptune.nexus.fcms.fxhouses.model.FxContract
import ly.neptune.nexus.fcms.fxhouses.model.FxContractsListFilter
import ly.neptune.nexus.fcms.fxhouses.model.FxHouse
import ly.neptune.nexus.fcms.fxhouses.model.FxPurchaseRequest
import ly.neptune.nexus.fcms.fxhouses.model.FxPurchaseRequestsListFilter
import ly.neptune.nexus.fcms.fxhouses.model.PendingPurchaseRequestsListFilter
import ly.neptune.nexus.fcms.fxhouses.model.PurchaseRequestsQueueListFilter
import ly.neptune.nexus.fcms.fxhouses.model.request.CashContractActionRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.CashContractProcessRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.CashContractRejectRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractActionRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractDeclineRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractProcessRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxPurchaseRequestApproveRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxPurchaseRequestDeclineRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxPurchaseRequestProcessRequest
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

    suspend fun approveFxPurchaseRequest(
        uuid: String,
        request: FxPurchaseRequestApproveRequest,
        options: RequestOptions? = null
    ): FxPurchaseRequest

    suspend fun processFxPurchaseRequest(
        uuid: String,
        request: FxPurchaseRequestProcessRequest,
        options: RequestOptions? = null
    ): FxPurchaseRequest

    suspend fun declineFxPurchaseRequest(
        uuid: String,
        request: FxPurchaseRequestDeclineRequest,
        options: RequestOptions? = null
    ): FxPurchaseRequest

    suspend fun listPendingPurchaseRequests(
        page: Int? = null,
        filter: PendingPurchaseRequestsListFilter? = null,
        options: RequestOptions? = null
    ): Page<FxPurchaseRequest>

    suspend fun listPurchaseRequestsQueue(
        page: Int? = null,
        filter: PurchaseRequestsQueueListFilter? = null,
        options: RequestOptions? = null
    ): Page<FxPurchaseRequest>

    suspend fun listCashContracts(
        page: Int? = null,
        filter: CashContractsListFilter? = null,
        options: RequestOptions? = null
    ): Page<CashContract>

    suspend fun approveCashContract(
        uuid: String,
        request: CashContractActionRequest,
        options: RequestOptions? = null
    ): CashContract

    suspend fun processCashContract(
        uuid: String,
        request: CashContractProcessRequest,
        options: RequestOptions? = null
    ): CashContract

    suspend fun rejectCashContract(
        uuid: String,
        request: CashContractRejectRequest,
        options: RequestOptions? = null
    ): CashContract
}
