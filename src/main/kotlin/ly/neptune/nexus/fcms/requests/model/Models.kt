package ly.neptune.nexus.fcms.requests.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
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
    @JsonProperty("bankAccount") val bankAccount: QueueBankAccount?,
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
