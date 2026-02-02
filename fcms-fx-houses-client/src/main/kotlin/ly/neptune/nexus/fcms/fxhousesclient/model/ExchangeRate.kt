package ly.neptune.nexus.fcms.fxhousesclient.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExchangeRate @JsonCreator constructor(
    @JsonProperty("date") val date: String?,
    @JsonProperty("rate") val rate: String?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)
