package ly.neptune.nexus.fcms.fxhousesclient.accounts

import ly.neptune.nexus.fcms.fxhousesclient.accounts.internal.FcmsFxBankAccountsClientImpl
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig

object FcmsFxBankAccountsClients {
    @JvmStatic
    fun create(config: FcmsFxConfig): FcmsFxBankAccountsClient = FcmsFxBankAccountsClientImpl(config)
}
