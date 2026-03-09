package ly.neptune.nexus.fcms.accounts.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class BankAccount @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String,
    @JsonProperty("iban") val iban: String?,
    @JsonProperty("account_number") val accountNumber: String?,
    @JsonProperty("currency") val currency: Currency?,
    @JsonProperty("state") val state: State?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("user") val user: User?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Currency @JsonCreator constructor(
    @JsonProperty("code") val code: String,
    @JsonProperty("name") val name: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class State @JsonCreator constructor(
    @JsonProperty("code") val code: String,
    @JsonProperty("name") val name: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class User @JsonCreator constructor(
    @JsonProperty("id") val id: Int?,
    @JsonProperty("first_name") val firstName: String?,
    @JsonProperty("father_name") val fatherName: String?,
    @JsonProperty("grandfather_name") val grandfatherName: String?,
    @JsonProperty("last_name") val lastName: String?,
    @JsonProperty("full_name_en") val fullNameEn: String?,
    @JsonProperty("nid") val nid: String?,
    @JsonProperty("passport_number") val passportNumber: String?,
    @JsonProperty("phone") val phone: String?,
    @JsonProperty("birth_date") val birthDate: String?,
    @JsonProperty("passport_expiry_date") val passportExpiryDate: String?,
)
