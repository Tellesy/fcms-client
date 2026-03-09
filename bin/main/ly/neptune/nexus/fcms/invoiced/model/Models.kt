package ly.neptune.nexus.fcms.invoiced.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

@JsonIgnoreProperties(ignoreUnknown = true)
data class InvoicedPurchaseRequest @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("reference") val reference: String?,
    @JsonProperty("state") val state: String?,
    @JsonProperty("amount") val amount: String?,
    @JsonProperty("sender_nid") val senderNid: String?,
    @JsonProperty("receiver_nid") val receiverNid: String?,
    @JsonProperty("sender_iban") val senderIban: String?,
    @JsonProperty("sender_phone_number") val senderPhoneNumber: String?,
    @JsonProperty("country_code") val countryCode: String?,
    @JsonProperty("invoice_number") val invoiceNumber: String?,
    @JsonProperty("beneficiary_name") val beneficiaryName: String?,
    @JsonProperty("beneficiary_account_number") val beneficiaryAccountNumber: String?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("updated_at") val updatedAt: String?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)
