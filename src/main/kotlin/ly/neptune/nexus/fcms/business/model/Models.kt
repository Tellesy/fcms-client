package ly.neptune.nexus.fcms.business.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

@JsonIgnoreProperties(ignoreUnknown = true)
data class BusinessPurchaseRequest @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("reference") val reference: String?,
    @JsonProperty("cbl_key") val cblKey: String?,
    @JsonProperty("iban") val iban: String?,
    @JsonProperty("amount") val amount: Long?,
    @JsonProperty("business_activity") val businessActivity: String?,
    @JsonProperty("state") val state: String?,
    @JsonProperty("timestamp") val timestamp: Long?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("updated_at") val updatedAt: String?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)
