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
