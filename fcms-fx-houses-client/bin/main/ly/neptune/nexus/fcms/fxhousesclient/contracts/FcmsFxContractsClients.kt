package ly.neptune.nexus.fcms.fxhousesclient.contracts

import ly.neptune.nexus.fcms.fxhousesclient.contracts.internal.FcmsFxContractsClientImpl
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig

object FcmsFxContractsClients {
    @JvmStatic
    fun create(config: FcmsFxConfig): FcmsFxContractsClient = FcmsFxContractsClientImpl(config)
}
