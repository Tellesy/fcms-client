package ly.neptune.nexus.fcms.fxhouses.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

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
    @JsonProperty("decline_reason") val declineReason: String? = null,
    @JsonProperty("decline_reason_note") val declineReasonNote: String? = null,
)
