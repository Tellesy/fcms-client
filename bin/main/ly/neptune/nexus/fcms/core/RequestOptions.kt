package ly.neptune.nexus.fcms.core

/**
 * Per-call request overrides.
 *
 * - baseUrlOverride: override base URL for this call (multi-tenant support)
 * - tokenOverride: override bearer token for this call
 * - readTimeoutMillisOverride: override read timeout for long-poll or heavy responses
 */
data class RequestOptions @JvmOverloads constructor(
    val baseUrlOverride: String? = null,
    val tokenOverride: String? = null,
    val readTimeoutMillisOverride: Long? = null,
)
