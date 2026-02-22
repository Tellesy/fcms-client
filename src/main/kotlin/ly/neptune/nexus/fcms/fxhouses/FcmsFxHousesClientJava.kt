package ly.neptune.nexus.fcms.fxhouses

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.accounts.model.BankAccount
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.fxhouses.internal.FcmsFxHousesClientImpl
import ly.neptune.nexus.fcms.fxhouses.model.FxContract
import ly.neptune.nexus.fcms.fxhouses.model.FxContractsListFilter
import ly.neptune.nexus.fcms.fxhouses.model.FxHouse
import ly.neptune.nexus.fcms.fxhouses.model.FxPurchaseRequest
import ly.neptune.nexus.fcms.fxhouses.model.FxPurchaseRequestsListFilter
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractActionRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractDeclineRequest
import ly.neptune.nexus.fcms.fxhouses.model.request.FxContractProcessRequest
import ly.neptune.nexus.fcms.salaries.model.Page
import java.util.concurrent.CompletableFuture

class FcmsFxHousesClientJava private constructor(
    private val delegate: FcmsFxHousesClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun listBankAccounts(page: Int?, options: RequestOptions?): CompletableFuture<Page<BankAccount>> =
        scope.future { delegate.listBankAccounts(page, options) }

    fun listFxHouses(page: Int?, options: RequestOptions?): CompletableFuture<Page<FxHouse>> =
        scope.future { delegate.listFxHouses(page, options) }

    fun listContracts(page: Int?, options: RequestOptions?): CompletableFuture<Page<FxContract>> =
        scope.future { delegate.listContracts(page, null, options) }

    fun listContracts(
        page: Int?,
        filter: FxContractsListFilter?,
        options: RequestOptions?
    ): CompletableFuture<Page<FxContract>> = scope.future { delegate.listContracts(page, filter, options) }

    fun listFxPurchaseRequests(
        page: Int?,
        filter: FxPurchaseRequestsListFilter?,
        options: RequestOptions?
    ): CompletableFuture<Page<FxPurchaseRequest>> =
        scope.future { delegate.listFxPurchaseRequests(page, filter, options) }

    fun approveContract(
        uuid: String,
        request: FxContractActionRequest,
        options: RequestOptions?
    ): CompletableFuture<FxContract> = scope.future { delegate.approveContract(uuid, request, options) }

    fun processContract(
        uuid: String,
        request: FxContractProcessRequest,
        options: RequestOptions?
    ): CompletableFuture<FxContract> = scope.future { delegate.processContract(uuid, request, options) }

    fun declineContract(
        uuid: String,
        request: FxContractDeclineRequest,
        options: RequestOptions?
    ): CompletableFuture<FxContract> = scope.future { delegate.declineContract(uuid, request, options) }

    override fun close() {
        scope.cancel("FcmsFxHousesClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsConfig): FcmsFxHousesClientJava {
            val impl: FcmsFxHousesClient = FcmsFxHousesClientImpl(config)
            return FcmsFxHousesClientJava(impl)
        }
    }
}
