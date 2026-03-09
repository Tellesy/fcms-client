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
 * 
 * Note: bank_transaction_timestamp MUST be serialized as a JSON string, not a number.
 */
data class CompleteTransactionRequest @JsonCreator constructor(
    @JsonProperty("bank_transaction_reference") val bankTransactionReference: String,
    @JsonProperty("bank_transaction_timestamp") val bankTransactionTimestamp: String,
) {
    /**
     * Secondary constructor accepting Long timestamp for convenience.
     * Converts to String internally as required by FCMS API.
     */
    constructor(
        bankTransactionReference: String,
        bankTransactionTimestamp: Long
    ) : this(bankTransactionReference, bankTransactionTimestamp.toString())
    
    companion object {
        private const val MILLIS_TO_SECONDS = 1000L
        
        /**
         * Create request with current timestamp (Unix epoch seconds).
         */
        @JvmStatic
        fun withCurrentTimestamp(bankTransactionReference: String): CompleteTransactionRequest =
            CompleteTransactionRequest(bankTransactionReference, System.currentTimeMillis() / MILLIS_TO_SECONDS)
    }
}
