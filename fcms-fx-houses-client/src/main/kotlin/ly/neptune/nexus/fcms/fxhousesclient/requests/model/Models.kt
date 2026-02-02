package ly.neptune.nexus.fcms.fxhousesclient.requests.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import ly.neptune.nexus.fcms.fxhousesclient.model.CodeName

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestType @JsonCreator constructor(
    @JsonProperty("code") val code: String?,
    @JsonProperty("name") val name: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestState @JsonCreator constructor(
    @JsonProperty("code") val code: String?,
    @JsonProperty("name") val name: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Currency @JsonCreator constructor(
    @JsonProperty("code") val code: String?,
    @JsonProperty("name") val name: String?,
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
    @JsonProperty("updated_at") val updatedAt: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BankAccount @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("iban") val iban: String?,
    @JsonProperty("currency") val currency: Currency?,
    @JsonProperty("account_number") val accountNumber: String?,
    @JsonProperty("state") val state: RequestState?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("user") val user: User?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BankBranch @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("is_active") val isActive: Boolean?,
    @JsonProperty("is_main_branch") val isMainBranch: Boolean?,
    @JsonProperty("created_at") val createdAt: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestActor @JsonCreator constructor(
    @JsonProperty("name") val name: String?,
    @JsonProperty("email") val email: String?,
    @JsonProperty("phone") val phone: String?,
    @JsonProperty("bank_branch") val bankBranch: BankBranch?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestExchangeRate @JsonCreator constructor(
    @JsonProperty("date") val date: String?,
    @JsonProperty("rate") val rate: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestQueueItem @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("reference") val reference: String?,
    @JsonProperty("amount_requested") val amountRequested: String?,
    @JsonProperty("type") val type: RequestType?,
    @JsonProperty("state") val state: RequestState?,
    @JsonProperty("processed_type") val processedType: CodeName?,
    @JsonProperty("bankAccount") val bankAccount: BankAccount?,
    @JsonProperty("bank_branch") val bankBranch: BankBranch?,
    @JsonProperty("exchange_rate") val exchangeRate: RequestExchangeRate?,
    @JsonProperty("approved_at") val approvedAt: String?,
    @JsonProperty("approved_by") val approvedBy: RequestActor?,
    @JsonProperty("declined_at") val declinedAt: String?,
    @JsonProperty("declined_by") val declinedBy: RequestActor?,
    @JsonProperty("processed_at") val processedAt: String?,
    @JsonProperty("processed_by") val processedBy: RequestActor?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("timestamp") val timestamp: Long?,
    @JsonProperty("deposit_type") val depositType: CodeName?,
    @JsonProperty("passport_attached") val passportAttached: Boolean?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequest @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("reference") val reference: String?,
    @JsonProperty("amount_requested") val amountRequested: String?,
    @JsonProperty("type") val type: RequestType?,
    @JsonProperty("state") val state: RequestState?,
    @JsonProperty("processed_type") val processedType: CodeName?,
    @JsonProperty("bankAccount") val bankAccount: BankAccount?,
    @JsonProperty("bank_branch") val bankBranch: BankBranch?,
    @JsonProperty("exchange_rate") val exchangeRate: RequestExchangeRate?,
    @JsonProperty("approved_at") val approvedAt: String?,
    @JsonProperty("approved_by") val approvedBy: RequestActor?,
    @JsonProperty("declined_at") val declinedAt: String?,
    @JsonProperty("declined_by") val declinedBy: RequestActor?,
    @JsonProperty("decline_reason") val declineReason: CodeName?,
    @JsonProperty("decline_reason_note") val declineReasonNote: String?,
    @JsonProperty("processed_at") val processedAt: String?,
    @JsonProperty("processed_by") val processedBy: RequestActor?,
    @JsonProperty("updated_from") val updatedFrom: String?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("timestamp") val timestamp: Long?,
    @JsonProperty("is_deleted") val isDeleted: Boolean?,
    @JsonProperty("deleted_at") val deletedAt: String?,
    @JsonProperty("deposit_type") val depositType: CodeName?,
    @JsonProperty("passport_attached") val passportAttached: Boolean?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeletedPurchaseRequest @JsonCreator constructor(
    @JsonProperty("uuid") val uuid: String?,
    @JsonProperty("iban") val iban: String?,
    @JsonProperty("reference") val reference: String?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("raw") val raw: JsonNode? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestsListFilter @JsonCreator constructor(
    @JsonProperty("state") val state: String? = null,
    @JsonProperty("type") val type: String? = null,
    @JsonProperty("reference") val reference: String? = null,
    @JsonProperty("iban") val iban: String? = null,
    @JsonProperty("created_on") val createdOn: String? = null,
    @JsonProperty("approved_on") val approvedOn: String? = null,
    @JsonProperty("processed_on") val processedOn: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestActionRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestDeclineRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
    @JsonProperty("decline_reason") val declineReason: String,
    @JsonProperty("decline_reason_note") val declineReasonNote: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PurchaseRequestProcessRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
    @JsonProperty("type") val type: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RefreshQueueResult @JsonCreator constructor(
    @JsonProperty("raw") val raw: JsonNode? = null,
)
