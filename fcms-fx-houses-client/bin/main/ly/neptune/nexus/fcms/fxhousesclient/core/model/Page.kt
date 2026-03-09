package ly.neptune.nexus.fcms.fxhousesclient.core.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Page<T> @JsonCreator constructor(
    @JsonProperty("data") val data: List<T>,
    @JsonProperty("total") val total: Long?,
    @JsonProperty("perPage") val perPage: Int?,
    @JsonProperty("currentPage") val currentPage: Int?,
    @JsonProperty("next") val next: String?,
    @JsonProperty("prev") val prev: String?,
)
