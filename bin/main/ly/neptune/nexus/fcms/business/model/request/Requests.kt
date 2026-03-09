package ly.neptune.nexus.fcms.business.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class BusinessPurchaseRequestCheckRequest @JsonCreator constructor(
    @JsonProperty("cbl_key") val cblKey: String,
    @JsonProperty("legal_representative_nid") val legalRepresentativeNid: String,
    @JsonProperty("business_activity") val businessActivity: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BusinessPurchaseRequestCreateRequest @JsonCreator constructor(
    @JsonProperty("cbl_key") val cblKey: String,
    @JsonProperty("legal_representative_nid") val legalRepresentativeNid: String,
    @JsonProperty("branch_uuid") val branchUuid: String,
    @JsonProperty("iban") val iban: String,
    @JsonProperty("amount") val amount: Long,
    @JsonProperty("business_activity") val businessActivity: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BusinessPurchaseRequestProcessRequest @JsonCreator constructor(
    @JsonProperty("ts") val ts: Long,
)
