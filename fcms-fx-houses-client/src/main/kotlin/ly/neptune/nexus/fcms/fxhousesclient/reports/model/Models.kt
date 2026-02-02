package ly.neptune.nexus.fcms.fxhousesclient.reports.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReportBucket @JsonCreator constructor(
    @JsonProperty("count") val count: Long?,
    @JsonProperty("total") val total: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestsStatesReport @JsonCreator constructor(
    @JsonProperty("approved") val approved: ReportBucket?,
    @JsonProperty("processed") val processed: ReportBucket?,
    @JsonProperty("declined") val declined: ReportBucket?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)
