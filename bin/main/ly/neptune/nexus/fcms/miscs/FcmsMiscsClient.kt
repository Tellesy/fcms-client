package ly.neptune.nexus.fcms.miscs

import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.miscs.model.BankBranch
import ly.neptune.nexus.fcms.miscs.model.CodeName
import ly.neptune.nexus.fcms.miscs.model.ExchangeRate
import ly.neptune.nexus.fcms.salaries.model.Page

interface FcmsMiscsClient : AutoCloseable {
    suspend fun listBankAccountRejectionReasons(options: RequestOptions? = null): List<CodeName>

    suspend fun listBusinessPurchaseRequestStates(options: RequestOptions? = null): List<CodeName>

    suspend fun listBankBranches(options: RequestOptions? = null): List<BankBranch>

    suspend fun listBusinessActivities(options: RequestOptions? = null): List<CodeName>

    suspend fun listCountries(options: RequestOptions? = null): List<CodeName>

    suspend fun listCurrencies(options: RequestOptions? = null): List<CodeName>

    suspend fun listCurrenciesLegacy(options: RequestOptions? = null): List<CodeName>

    suspend fun listExchangeRates(page: Int? = null, filterDate: String? = null, options: RequestOptions? = null): Page<ExchangeRate>

    suspend fun listInvoicedPurchaseRequestStates(options: RequestOptions? = null): List<CodeName>

    suspend fun listPurchaseRequestDeclineReasons(options: RequestOptions? = null): List<CodeName>

    suspend fun listPurchaseRequestStates(options: RequestOptions? = null): List<CodeName>

    suspend fun listPurchaseRequestTypes(options: RequestOptions? = null): List<CodeName>

    suspend fun listBankAccountStates(options: RequestOptions? = null): List<CodeName>

    suspend fun listContractRejectReasons(options: RequestOptions? = null): List<CodeName>
}
