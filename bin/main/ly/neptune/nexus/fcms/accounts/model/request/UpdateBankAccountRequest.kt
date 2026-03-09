package ly.neptune.nexus.fcms.accounts.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Request payload for updating a bank account details.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateBankAccountRequest @JsonCreator constructor(
    @JsonProperty("account_number") val accountNumber: String? = null,
    @JsonProperty("currency_code") val currencyCode: String? = null,
)
