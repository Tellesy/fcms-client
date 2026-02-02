package ly.neptune.nexus.fcms.miscs

import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.miscs.internal.FcmsMiscsClientImpl

object FcmsMiscsClients {
    @JvmStatic
    fun create(config: FcmsConfig): FcmsMiscsClient = FcmsMiscsClientImpl(config)
}
