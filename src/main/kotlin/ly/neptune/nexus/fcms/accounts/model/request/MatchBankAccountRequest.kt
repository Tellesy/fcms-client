package ly.neptune.nexus.fcms.accounts.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Request payload for matching a bank account to a customer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchBankAccountRequest @JsonCreator constructor(
    @JsonProperty("nid") val nid: String? = null,
    @JsonProperty("phone") val phone: String? = null,
    @JsonProperty("passport_number") val passportNumber: String? = null,
    @JsonProperty("account_number") val accountNumber: String? = null,
    @JsonProperty("currency_code") val currencyCode: String? = null,
)
