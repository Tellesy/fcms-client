package ly.neptune.nexus.fcms.fxhousesclient.miscs

import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.miscs.internal.FcmsFxMiscsClientImpl

object FcmsFxMiscsClients {
    @JvmStatic
    fun create(config: FcmsFxConfig): FcmsFxMiscsClient = FcmsFxMiscsClientImpl(config)
}
