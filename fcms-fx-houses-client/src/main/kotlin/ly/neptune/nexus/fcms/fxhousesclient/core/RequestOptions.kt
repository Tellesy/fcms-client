package ly.neptune.nexus.fcms.fxhousesclient.core

/**
 * Per-request overrides for FX Houses client.
 */
data class RequestOptions(
    val tokenOverride: String? = null,
    val readTimeoutMillisOverride: Long? = null,
    val baseUrlOverride: String? = null,
)
