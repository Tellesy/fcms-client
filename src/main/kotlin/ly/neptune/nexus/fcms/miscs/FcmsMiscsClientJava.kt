package ly.neptune.nexus.fcms.miscs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.miscs.internal.FcmsMiscsClientImpl
import ly.neptune.nexus.fcms.miscs.model.BankBranch
import ly.neptune.nexus.fcms.miscs.model.CodeName
import ly.neptune.nexus.fcms.miscs.model.ExchangeRate
import ly.neptune.nexus.fcms.salaries.model.Page
import java.util.concurrent.CompletableFuture

class FcmsMiscsClientJava private constructor(
    private val delegate: FcmsMiscsClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun listBankAccountRejectionReasons(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listBankAccountRejectionReasons(options) }

    fun listBusinessPurchaseRequestStates(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listBusinessPurchaseRequestStates(options) }

    fun listBankBranches(options: RequestOptions?): CompletableFuture<List<BankBranch>> =
        scope.future { delegate.listBankBranches(options) }

    fun listBusinessActivities(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listBusinessActivities(options) }

    fun listCountries(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listCountries(options) }

    fun listCurrencies(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listCurrencies(options) }

    fun listCurrenciesLegacy(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listCurrenciesLegacy(options) }

    fun listExchangeRates(page: Int?, filterDate: String?, options: RequestOptions?): CompletableFuture<Page<ExchangeRate>> =
        scope.future { delegate.listExchangeRates(page, filterDate, options) }

    fun listInvoicedPurchaseRequestStates(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listInvoicedPurchaseRequestStates(options) }

    fun listPurchaseRequestDeclineReasons(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listPurchaseRequestDeclineReasons(options) }

    fun listPurchaseRequestStates(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listPurchaseRequestStates(options) }

    fun listPurchaseRequestTypes(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listPurchaseRequestTypes(options) }

    fun listBankAccountStates(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listBankAccountStates(options) }

    fun listContractRejectReasons(options: RequestOptions?): CompletableFuture<List<CodeName>> =
        scope.future { delegate.listContractRejectReasons(options) }

    override fun close() {
        scope.cancel("FcmsMiscsClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsConfig): FcmsMiscsClientJava {
            val impl: FcmsMiscsClient = FcmsMiscsClientImpl(config)
            return FcmsMiscsClientJava(impl)
        }
    }
}
