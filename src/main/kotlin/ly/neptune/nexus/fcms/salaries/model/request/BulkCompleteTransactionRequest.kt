package ly.neptune.nexus.fcms.salaries.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Request body for bulk completing transactions.
 *
 * JSON:
 * {
 *   "transactions": [
 *     {
 *       "uuid": "...",
 *       "bank_transaction_reference": "...",
 *       "bank_transaction_timestamp": 1234567890
 *     }
 *   ]
 * }
 */
data class BulkCompleteTransactionRequest @JsonCreator constructor(
    @JsonProperty("transactions") val transactions: List<BulkCompleteItem>,
)

/**
 * Individual transaction item for bulk completion.
 */
data class BulkCompleteItem @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String,
    @JsonProperty("bank_transaction_reference") val bankTransactionReference: String,
    @JsonProperty("bank_transaction_timestamp") val bankTransactionTimestamp: Long,
)
