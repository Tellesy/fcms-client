package ly.neptune.nexus.fcms.salaries

import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.salaries.internal.FcmsSalariesClientImpl

/** Factory for creating [FcmsSalariesClient] instances. */
object FcmsSalariesClients {
    /** Create a new client with the given [config]. */
    @JvmStatic
    fun create(config: FcmsConfig): FcmsSalariesClient = FcmsSalariesClientImpl(config)
}
