package ly.neptune.nexus.fcms.fxhousesclient.reports

import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.reports.internal.FcmsFxReportsClientImpl

object FcmsFxReportsClients {
    @JvmStatic
    fun create(config: FcmsFxConfig): FcmsFxReportsClient = FcmsFxReportsClientImpl(config)
}
