package ly.neptune.nexus.fcms.business

import ly.neptune.nexus.fcms.business.model.BusinessPurchaseRequest
import ly.neptune.nexus.fcms.business.model.request.BusinessPurchaseRequestCheckRequest
import ly.neptune.nexus.fcms.business.model.request.BusinessPurchaseRequestCreateRequest
import ly.neptune.nexus.fcms.business.model.request.BusinessPurchaseRequestProcessRequest
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.salaries.model.Page
import java.io.File

interface FcmsBusinessPurchaseRequestsClient : AutoCloseable {
    suspend fun check(request: BusinessPurchaseRequestCheckRequest, options: RequestOptions? = null): BusinessPurchaseRequest

    suspend fun list(
        page: Int? = null,
        filter: Map<String, String> = emptyMap(),
        options: RequestOptions? = null,
    ): Page<BusinessPurchaseRequest>

    suspend fun show(uuid: String, options: RequestOptions? = null): BusinessPurchaseRequest

    suspend fun create(request: BusinessPurchaseRequestCreateRequest, options: RequestOptions? = null): BusinessPurchaseRequest

    suspend fun close(uuid: String, attachments: List<File> = emptyList(), options: RequestOptions? = null): BusinessPurchaseRequest

    suspend fun process(uuid: String, request: BusinessPurchaseRequestProcessRequest, options: RequestOptions? = null): BusinessPurchaseRequest
}
