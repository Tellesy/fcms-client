package ly.neptune.nexus.fcms.fxhouses

import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.fxhouses.internal.FcmsFxHousesClientImpl

object FcmsFxHousesClients {
    @JvmStatic
    fun create(config: FcmsConfig): FcmsFxHousesClient = FcmsFxHousesClientImpl(config)
}
