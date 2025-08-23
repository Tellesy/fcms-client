package ly.neptune.nexus.fcms.salaries.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.math.BigDecimal

/**
 * A salary payout transaction received from the FCMS API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Transaction @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String,
    @JsonProperty("state") val state: String,
    @JsonProperty("individual") val individual: Individual,
    @JsonProperty("bankAccount") val bankAccount: BankAccount,
    @JsonProperty("salary") val salary: Salary,
)

/**
 * Employee/individual information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Individual @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("nid") val nid: String,
    @JsonProperty("mofFinancialNumber") val mofFinancialNumber: String,
    @JsonProperty("phoneNumber") val phoneNumber: String?,
)

/**
 * Bank account information for the payee.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BankAccount @JsonCreator constructor(
    @JsonProperty("number") val number: String,
    @JsonProperty("iban") val iban: String?,
)

/**
 * Salary details, including amount (parsed as BigDecimal), currency and period.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Salary @JsonCreator constructor(
    @JsonProperty("amount") @JsonDeserialize(using = StringBigDecimalDeserializer::class) val amount: BigDecimal,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("period") val period: Period,
)

/**
 * Salary period (year/month) for which the payout applies.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Period @JsonCreator constructor(
    @JsonProperty("year") val year: String,
    @JsonProperty("month") val month: String,
)

/**
 * A supported rejection reason returned by the API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class RejectionReason @JsonCreator constructor(
    @JsonProperty("code") val code: String,
    @JsonProperty("name") val name: String,
)

/**
 * Generic paginated response abstraction used by the SDK.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Page<T> @JsonCreator constructor(
    @JsonProperty("data") val data: List<T>,
    @JsonProperty("total") val total: Long?,
    @JsonProperty("perPage") val perPage: Int?,
    @JsonProperty("currentPage") val currentPage: Int?,
    @JsonProperty("next") val next: String?,
    @JsonProperty("prev") val prev: String?,
)
