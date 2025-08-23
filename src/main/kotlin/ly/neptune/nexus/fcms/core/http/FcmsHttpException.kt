package ly.neptune.nexus.fcms.core.http

/**
 * HTTP exception for FCMS API non-2xx responses.
 *
 * @property status HTTP status code
 * @property code Optional application-level error code parsed from body
 * @property message Optional error message parsed from body
 * @property body Raw response body (if available)
 * @property headers Response headers as a map
 * @property retryAfterSeconds Parsed Retry-After header in seconds, if present
 */
class FcmsHttpException(
    val status: Int,
    val code: String?,
    override val message: String?,
    val body: String?,
    val headers: Map<String, String>,
    val retryAfterSeconds: Long? = null,
) : RuntimeException(message)
