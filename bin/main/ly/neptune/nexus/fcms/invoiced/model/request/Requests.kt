package ly.neptune.nexus.fcms.invoiced.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class InvoicedPurchaseRequestCreateRequest @JsonCreator constructor(
    @JsonProperty("sender_nid") val senderNid: String,
    @JsonProperty("receiver_nid") val receiverNid: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("beneficiary_name") val beneficiaryName: String,
    @JsonProperty("beneficiary_account_number") val beneficiaryAccountNumber: String,
    @JsonProperty("invoice_number") val invoiceNumber: String,
    @JsonProperty("sender_iban") val senderIban: String,
    @JsonProperty("sender_phone_number") val senderPhoneNumber: String,
    @JsonProperty("country_code") val countryCode: String,
    @JsonProperty("amount") val amount: String,
    @JsonProperty("branch_uuid") val branchUuid: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InvoicedPurchaseRequestProcessRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
)
