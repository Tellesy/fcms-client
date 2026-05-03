package ly.neptune.nexus.fcms.fxhouses.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import ly.neptune.nexus.fcms.accounts.model.User

@JsonIgnoreProperties(ignoreUnknown = true)
data class FxHouse @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("bank_account") val bankAccount: FxHouseBankAccount?,
    @JsonProperty("cbl_key") val cblKey: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FxHouseBankAccount @JsonCreator constructor(
    @JsonProperty("account_number") val accountNumber: String?,
    @JsonProperty("iban") val iban: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FxContract @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("amount") val amount: String?,
    @JsonProperty("cash_price") val cashPrice: String?,
    @JsonProperty("bank_transfer_price") val bankTransferPrice: String?,
    @JsonProperty("fx_fee") val fxFee: String?,
    @JsonProperty("sum_of_approved_cash_price") val sumOfApprovedCashPrice: String?,
    @JsonProperty("sum_of_approved_bank_transfer_price") val sumOfApprovedBankTransferPrice: String?,
    @JsonProperty("sum_of_approved_cash_amount") val sumOfApprovedCashAmount: String?,
    @JsonProperty("sum_of_approved_bank_transfer_amount") val sumOfApprovedBankTransferAmount: String?,
    @JsonProperty("date") val date: String?,
    @JsonProperty("state") val state: String?,
    @JsonProperty("timestamp") val timestamp: Long?,
    @JsonProperty("bank_account") val bankAccount: FxHouseBankAccount?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("updated_at") val updatedAt: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CodeName @JsonCreator constructor(
    @JsonProperty("code") val code: String?,
    @JsonProperty("name") val name: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExchangeRate @JsonCreator constructor(
    @JsonProperty("date") val date: String?,
    @JsonProperty("rate") val rate: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BankBranch @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("name") val name: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UsdProviderBranch @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("is_active") val isActive: String?,
    @JsonProperty("is_main_branch") val isMainBranch: String?,
    @JsonProperty("created_at") val createdAt: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FxPurchaseRequest @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("reference") val reference: String?,
    @JsonProperty("amount_requested") val amountRequested: String?,
    @JsonProperty("cost") val cost: String?,
    @JsonProperty("type") val type: CodeName?,
    @JsonProperty("state") val state: CodeName?,
    @JsonProperty("processed_type") val processedType: CodeName?,
    @JsonProperty("contract") val contract: FxContract?,
    @JsonProperty("company") val company: FxHouse?,
    @JsonAlias("bankAccount", "bank_account") val bankAccount: ly.neptune.nexus.fcms.accounts.model.BankAccount?,
    @JsonProperty("bank_branch") val bankBranch: BankBranch?,
    @JsonProperty("deposit_type") val depositType: CodeName?,
    @JsonProperty("passport_attached") val passportAttached: Boolean?,
    @JsonProperty("exchange_rate") val exchangeRate: ExchangeRate?,
    @JsonProperty("processed_at") val processedAt: String?,
    @JsonProperty("processed_by") val processedBy: String?,
    @JsonProperty("updated_from") val updatedFrom: String?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("timestamp") val timestamp: Long?,
    @JsonProperty("is_deleted") val isDeleted: Boolean?,
    @JsonProperty("deleted_at") val deletedAt: String?,
    @JsonProperty("bank_processed_at") val bankProcessedAt: String?,
    @JsonProperty("bank_approved_at") val bankApprovedAt: String?,
    @JsonProperty("bank_declined_at") val bankDeclinedAt: String?,
    @JsonProperty("usd_provider_branch") val usdProviderBranch: UsdProviderBranch?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)

data class FxContractsListFilter(
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val state: String? = null,
    val cblKey: String? = null,
)

data class FxPurchaseRequestsListFilter(
    val approvedOn: String? = null,
    val state: String? = null,
    val type: String? = null,
    val reference: String? = null,
)

data class PendingPurchaseRequestsListFilter(
    val reference: String? = null,
    val phone: String? = null,
    val nid: String? = null,
)

data class PurchaseRequestsQueueListFilter(
    val type: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CashContract @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("amount") val amount: String?,
    @JsonProperty("cash_price") val cashPrice: String?,
    @JsonProperty("bank_transfer_price") val bankTransferPrice: String?,
    @JsonProperty("fx_fee") val fxFee: String?,
    @JsonProperty("sum_of_approved_cash_price") val sumOfApprovedCashPrice: String?,
    @JsonProperty("sum_of_approved_bank_transfer_price") val sumOfApprovedBankTransferPrice: String?,
    @JsonProperty("sum_of_approved_cash_amount") val sumOfApprovedCashAmount: String?,
    @JsonProperty("sum_of_approved_bank_transfer_amount") val sumOfApprovedBankTransferAmount: String?,
    @JsonProperty("date") val date: String?,
    @JsonProperty("state") val state: String?,
    @JsonProperty("timestamp") val timestamp: Long?,
    @JsonProperty("bank_account") val bankAccount: FxHouseBankAccount?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("updated_at") val updatedAt: String?,
    @JsonProperty("company") val company: FxHouse?,
)

data class CashContractsListFilter(
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val state: String? = null,
    val cblKey: String? = null,
)
