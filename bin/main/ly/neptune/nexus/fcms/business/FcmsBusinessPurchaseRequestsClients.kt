package ly.neptune.nexus.fcms.business

import ly.neptune.nexus.fcms.business.internal.FcmsBusinessPurchaseRequestsClientImpl
import ly.neptune.nexus.fcms.core.FcmsConfig

object FcmsBusinessPurchaseRequestsClients {
    @JvmStatic
    fun create(config: FcmsConfig): FcmsBusinessPurchaseRequestsClient = FcmsBusinessPurchaseRequestsClientImpl(config)
}
