package ly.neptune.nexus.fcms.accounts

import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.accounts.internal.FcmsAccountsClientImpl

/** Factory for creating [FcmsAccountsClient] instances. */
object FcmsAccountsClients {
    /** Create a new client with the given [config]. */
    @JvmStatic
    fun create(config: FcmsConfig): FcmsAccountsClient = FcmsAccountsClientImpl(config)
}
