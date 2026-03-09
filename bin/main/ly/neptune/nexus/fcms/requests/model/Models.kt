package ly.neptune.nexus.fcms.requests.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import ly.neptune.nexus.fcms.accounts.model.User

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestQueueItem @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String,
    @JsonProperty("amount_requested") val amountRequested: Long?,
    @JsonProperty("type") val type: RequestType?,
    @JsonProperty("state") val state: RequestState?,
    @JsonProperty("processed_type") val processedType: String?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("timestamp") val timestamp: Long?,
    @JsonProperty("bank_account") val bankAccount: QueueBankAccount?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestType @JsonCreator constructor(
    @JsonProperty("code") val code: Int?,
    @JsonProperty("name") val name: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestState @JsonCreator constructor(
    @JsonProperty("code") val code: String?,
    @JsonProperty("name") val name: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class QueueBankAccount @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("iban") val iban: String?,
    @JsonProperty("state") val state: RequestState?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("user") val user: User?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestsListFilter @JsonCreator constructor(
    @JsonProperty("state") val state: String? = null,
    @JsonProperty("type") val type: String? = null,
    @JsonProperty("reference") val reference: String? = null,
    @JsonProperty("iban") val iban: String? = null,
    @JsonProperty("created_on") val createdOn: String? = null,
    @JsonProperty("approved_on") val approvedOn: String? = null,
    @JsonProperty("processed_on") val processedOn: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequest @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("reference") val reference: String?,
    @JsonProperty("amount_requested") val amountRequested: Long?,
    @JsonProperty("type") val type: RequestType?,
    @JsonProperty("state") val state: RequestState?,
    @JsonProperty("processed_type") val processedType: String?,
    @JsonProperty("timestamp") val timestamp: Long?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("updated_at") val updatedAt: String?,
    @JsonProperty("bank_account") val bankAccount: QueueBankAccount?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeletedPurchaseRequest @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("iban") val iban: String?,
    @JsonProperty("reference") val reference: String?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestActionRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestDeclineRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
    @JsonProperty("decline_reason") val declineReason: String,
    @JsonProperty("decline_reason_note") val declineReasonNote: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestProcessRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
    @JsonProperty("type") val type: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RefreshQueueResult @JsonCreator constructor(
    @JsonProperty("raw") val raw: JsonNode? = null,
)
