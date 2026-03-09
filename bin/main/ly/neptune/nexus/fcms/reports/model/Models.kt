package ly.neptune.nexus.fcms.reports.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class StateCountTotal @JsonCreator constructor(
    @JsonProperty("count") val count: Long?,
    @JsonProperty("total") val total: Long?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestsStatesSummary @JsonCreator constructor(
    @JsonProperty("approved") val approved: StateCountTotal?,
    @JsonProperty("processed") val processed: StateCountTotal?,
    @JsonProperty("declined") val declined: StateCountTotal?,
)
