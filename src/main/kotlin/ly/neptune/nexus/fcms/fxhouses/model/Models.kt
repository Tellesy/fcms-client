package ly.neptune.nexus.fcms.fxhouses.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

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
    @JsonProperty("sum_of_approved_cash_price") val sumOfApprovedCashPrice: String?,
    @JsonProperty("sum_of_approved_bank_transfer_price") val sumOfApprovedBankTransferPrice: String?,
    @JsonProperty("date") val date: String?,
    @JsonProperty("state") val state: String?,
    @JsonProperty("timestamp") val timestamp: Long?,
    @JsonProperty("bank_account") val bankAccount: FxHouseBankAccount?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("updated_at") val updatedAt: String?,
)
