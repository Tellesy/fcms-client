package ly.neptune.nexus.fcms.fxhousesclient.contracts.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import ly.neptune.nexus.fcms.fxhousesclient.model.CodeName

@JsonIgnoreProperties(ignoreUnknown = true)
data class FxContract @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String? = null,
    @JsonProperty("amount") val amount: String?,
    @JsonProperty("cash_price") val cashPrice: String?,
    @JsonProperty("bank_transfer_price") val bankTransferPrice: String?,
    @JsonProperty("rate") val rate: String? = null,
    @JsonProperty("state") val state: CodeName?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FxContractCreateRequest @JsonCreator constructor(
    @JsonProperty("cash_price") val cashPrice: String,
    @JsonProperty("bank_transfer_price") val bankTransferPrice: String,
    @JsonProperty("amount") val amount: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FxContractActionRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FxContractProcessRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
    @JsonProperty("amount_purchased_cash") val amountPurchasedCash: String? = null,
    @JsonProperty("amount_purchased_bank_transfer") val amountPurchasedBankTransfer: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FxContractDeclineRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
    @JsonProperty("reject_reason") val rejectReason: String? = null,
    @JsonProperty("reject_reason_note") val rejectReasonNote: String? = null,
)

data class FxContractsListFilter(
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val state: String? = null,
    val cblKey: String? = null,
)
