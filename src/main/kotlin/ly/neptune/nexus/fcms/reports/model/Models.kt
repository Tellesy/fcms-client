package ly.neptune.nexus.fcms.reports.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestsStatesReportRow @JsonCreator constructor(
    @JsonProperty("state") val state: String?,
    @JsonProperty("count") val count: Long?,
    @JsonProperty("amount") val amount: String?,
)
