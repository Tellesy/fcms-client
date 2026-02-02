package ly.neptune.nexus.fcms.business

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.business.internal.FcmsBusinessPurchaseRequestsClientImpl
import ly.neptune.nexus.fcms.business.model.BusinessPurchaseRequest
import ly.neptune.nexus.fcms.business.model.request.BusinessPurchaseRequestCheckRequest
import ly.neptune.nexus.fcms.business.model.request.BusinessPurchaseRequestCreateRequest
import ly.neptune.nexus.fcms.business.model.request.BusinessPurchaseRequestProcessRequest
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.salaries.model.Page
import java.io.File
import java.util.concurrent.CompletableFuture

class FcmsBusinessPurchaseRequestsClientJava private constructor(
    private val delegate: FcmsBusinessPurchaseRequestsClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun check(request: BusinessPurchaseRequestCheckRequest, options: RequestOptions?): CompletableFuture<BusinessPurchaseRequest> =
        scope.future { delegate.check(request, options) }

    fun list(
        page: Int?,
        filter: Map<String, String>,
        options: RequestOptions?,
    ): CompletableFuture<Page<BusinessPurchaseRequest>> =
        scope.future { delegate.list(page, filter, options) }

    fun show(uuid: String, options: RequestOptions?): CompletableFuture<BusinessPurchaseRequest> =
        scope.future { delegate.show(uuid, options) }

    fun create(request: BusinessPurchaseRequestCreateRequest, options: RequestOptions?): CompletableFuture<BusinessPurchaseRequest> =
        scope.future { delegate.create(request, options) }

    fun close(uuid: String, attachments: List<File>, options: RequestOptions?): CompletableFuture<BusinessPurchaseRequest> =
        scope.future { delegate.close(uuid, attachments, options) }

    fun process(uuid: String, request: BusinessPurchaseRequestProcessRequest, options: RequestOptions?): CompletableFuture<BusinessPurchaseRequest> =
        scope.future { delegate.process(uuid, request, options) }

    override fun close() {
        scope.cancel("FcmsBusinessPurchaseRequestsClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsConfig): FcmsBusinessPurchaseRequestsClientJava {
            val impl: FcmsBusinessPurchaseRequestsClient = FcmsBusinessPurchaseRequestsClientImpl(config)
            return FcmsBusinessPurchaseRequestsClientJava(impl)
        }
    }
}
