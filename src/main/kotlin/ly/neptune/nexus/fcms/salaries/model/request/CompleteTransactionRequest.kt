package ly.neptune.nexus.fcms.salaries.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Request body for completing a transaction.
 *
 * JSON:
 * {
 *   "bank_transaction_reference": "...",
 *   "bank_transaction_timestamp": "..."
 * }
 */
data class CompleteTransactionRequest @JsonCreator constructor(
    @JsonProperty("bank_transaction_reference") val bankTransactionReference: String,
    @JsonProperty("bank_transaction_timestamp") val bankTransactionTimestamp: String,
)
