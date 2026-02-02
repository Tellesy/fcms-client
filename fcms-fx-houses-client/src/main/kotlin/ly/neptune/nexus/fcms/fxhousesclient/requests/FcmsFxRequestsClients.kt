package ly.neptune.nexus.fcms.fxhousesclient.requests

import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.requests.internal.FcmsFxRequestsClientImpl

object FcmsFxRequestsClients {
    @JvmStatic
    fun create(config: FcmsFxConfig): FcmsFxRequestsClient = FcmsFxRequestsClientImpl(config)
}
