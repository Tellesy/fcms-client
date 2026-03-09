package ly.neptune.nexus.fcms.invoiced

import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.invoiced.internal.FcmsInvoicedPurchaseRequestsClientImpl

object FcmsInvoicedPurchaseRequestsClients {
    @JvmStatic
    fun create(config: FcmsConfig): FcmsInvoicedPurchaseRequestsClient = FcmsInvoicedPurchaseRequestsClientImpl(config)
}
