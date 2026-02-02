package ly.neptune.nexus.fcms.invoiced

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.invoiced.internal.FcmsInvoicedPurchaseRequestsClientImpl
import ly.neptune.nexus.fcms.invoiced.model.InvoicedPurchaseRequest
import ly.neptune.nexus.fcms.invoiced.model.request.InvoicedPurchaseRequestCreateRequest
import ly.neptune.nexus.fcms.invoiced.model.request.InvoicedPurchaseRequestProcessRequest
import ly.neptune.nexus.fcms.salaries.model.Page
import java.io.File
import java.util.concurrent.CompletableFuture

class FcmsInvoicedPurchaseRequestsClientJava private constructor(
    private val delegate: FcmsInvoicedPurchaseRequestsClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun create(
        request: InvoicedPurchaseRequestCreateRequest,
        attachments: List<File>,
        options: RequestOptions?
    ): CompletableFuture<InvoicedPurchaseRequest> =
        scope.future { delegate.create(request, attachments, options) }

    fun list(
        page: Int?,
        filter: Map<String, String>,
        options: RequestOptions?
    ): CompletableFuture<Page<InvoicedPurchaseRequest>> =
        scope.future { delegate.list(page, filter, options) }

    fun show(uuid: String, options: RequestOptions?): CompletableFuture<InvoicedPurchaseRequest> =
        scope.future { delegate.show(uuid, options) }

    fun process(uuid: String, request: InvoicedPurchaseRequestProcessRequest, options: RequestOptions?): CompletableFuture<InvoicedPurchaseRequest> =
        scope.future { delegate.process(uuid, request, options) }

    override fun close() {
        scope.cancel("FcmsInvoicedPurchaseRequestsClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsConfig): FcmsInvoicedPurchaseRequestsClientJava {
            val impl: FcmsInvoicedPurchaseRequestsClient = FcmsInvoicedPurchaseRequestsClientImpl(config)
            return FcmsInvoicedPurchaseRequestsClientJava(impl)
        }
    }
}
