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
    @JsonProperty("uuid") @JsonDeserialize(using = StringDeserializer::class) val uuid: String,
    @JsonProperty("state") @JsonDeserialize(using = StringDeserializer::class) val state: String,
    @JsonProperty("individual") val individual: Individual,
    @JsonProperty("bankAccount") val bankAccount: BankAccount,
    @JsonProperty("salary") val salary: Salary,
    @JsonProperty("entity") val entity: Entity?,
    @JsonProperty("description") val description: String?,
)

/**
 * Employee/individual information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Individual @JsonCreator constructor(
    @JsonProperty("name") @JsonDeserialize(using = StringDeserializer::class) val name: String,
    @JsonProperty("nid") @JsonDeserialize(using = StringDeserializer::class) val nid: String,
    @JsonProperty("mofFinancialNumber")
    @JsonDeserialize(using = StringDeserializer::class)
    val mofFinancialNumber: String,
    @JsonProperty("phoneNumber") val phoneNumber: String?,
)

/**
 * Bank account information for the payee.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BankAccount @JsonCreator constructor(
    @JsonProperty("number") @JsonDeserialize(using = StringDeserializer::class) val number: String,
    @JsonProperty("iban") val iban: String?,
    @JsonProperty("bankBranch") val bankBranch: String?,
)

/**
 * Salary details, including amount (parsed as BigDecimal), currency and period.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Salary @JsonCreator constructor(
    @JsonProperty("amount") @JsonDeserialize(using = StringBigDecimalDeserializer::class) val amount: BigDecimal,
    @JsonProperty("currency") @JsonDeserialize(using = StringDeserializer::class) val currency: String,
    @JsonProperty("period") val period: Period,
)

/**
 * Entity (organization) information associated with the transaction.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Entity @JsonCreator constructor(
    @JsonProperty("name") val name: String?,
    @JsonProperty("region") val region: String?,
)

/**
 * Salary period (year/month) for which the payout applies.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Period @JsonCreator constructor(
    @JsonProperty("year") @JsonDeserialize(using = StringDeserializer::class) val year: String,
    @JsonProperty("month") @JsonDeserialize(using = StringDeserializer::class) val month: String,
)

/**
 * A supported rejection reason returned by the API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class RejectionReason @JsonCreator constructor(
    @JsonProperty("code") @JsonDeserialize(using = StringDeserializer::class) val code: String,
    @JsonProperty("name") @JsonDeserialize(using = StringDeserializer::class) val name: String,
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
