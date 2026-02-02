package ly.neptune.nexus.fcms.reports

import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.reports.internal.FcmsReportsClientImpl

object FcmsReportsClients {
    @JvmStatic
    fun create(config: FcmsConfig): FcmsReportsClient = FcmsReportsClientImpl(config)
}
