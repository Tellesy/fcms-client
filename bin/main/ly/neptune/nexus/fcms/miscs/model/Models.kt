package ly.neptune.nexus.fcms.miscs.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

@JsonIgnoreProperties(ignoreUnknown = true)
data class CodeName @JsonCreator constructor(
    @JsonProperty("code") val code: String?,
    @JsonProperty("name") val name: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BankBranch @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("code") val code: String?,
    @JsonProperty("name") val name: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExchangeRate @JsonCreator constructor(
    @JsonProperty("base") val base: String? = null,
    @JsonProperty("quote") val quote: String? = null,
    @JsonProperty("rate") val rate: String? = null,
    @JsonProperty("date") val date: String? = null,
    @JsonProperty("raw") val raw: JsonNode? = null,
)
