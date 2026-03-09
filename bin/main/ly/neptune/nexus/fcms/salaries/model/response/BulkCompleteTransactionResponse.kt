package ly.neptune.nexus.fcms.salaries.model.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Response for bulk complete transactions endpoint.
 * Contains lists of successful and failed transaction completions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BulkCompleteTransactionResponse @JsonCreator constructor(
    @JsonProperty("successful") val successful: List<BulkCompleteResultItem>?,
    @JsonProperty("failed") val failed: List<BulkCompleteFailedItem>?,
)

/**
 * Successful bulk completion item.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BulkCompleteResultItem @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String,
    @JsonProperty("state") val state: String?,
)

/**
 * Failed bulk completion item with error details.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BulkCompleteFailedItem @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String,
    @JsonProperty("error") val error: String?,
)
