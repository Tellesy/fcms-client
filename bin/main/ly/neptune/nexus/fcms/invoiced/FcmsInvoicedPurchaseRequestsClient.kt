package ly.neptune.nexus.fcms.invoiced

import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.invoiced.model.InvoicedPurchaseRequest
import ly.neptune.nexus.fcms.invoiced.model.request.InvoicedPurchaseRequestCreateRequest
import ly.neptune.nexus.fcms.invoiced.model.request.InvoicedPurchaseRequestProcessRequest
import ly.neptune.nexus.fcms.salaries.model.Page
import java.io.File

interface FcmsInvoicedPurchaseRequestsClient : AutoCloseable {
    suspend fun create(
        request: InvoicedPurchaseRequestCreateRequest,
        attachments: List<File> = emptyList(),
        options: RequestOptions? = null,
    ): InvoicedPurchaseRequest

    suspend fun list(
        page: Int? = null,
        filter: Map<String, String> = emptyMap(),
        options: RequestOptions? = null,
    ): Page<InvoicedPurchaseRequest>

    suspend fun show(uuid: String, options: RequestOptions? = null): InvoicedPurchaseRequest

    suspend fun process(uuid: String, request: InvoicedPurchaseRequestProcessRequest, options: RequestOptions? = null): InvoicedPurchaseRequest
}
