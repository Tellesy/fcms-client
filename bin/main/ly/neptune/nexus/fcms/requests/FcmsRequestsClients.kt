package ly.neptune.nexus.fcms.requests

import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.requests.internal.FcmsRequestsClientImpl

/** Factory for creating [FcmsRequestsClient] instances. */
object FcmsRequestsClients {
    /** Create a new client with the given [config]. */
    @JvmStatic
    fun create(config: FcmsConfig): FcmsRequestsClient = FcmsRequestsClientImpl(config)
}
