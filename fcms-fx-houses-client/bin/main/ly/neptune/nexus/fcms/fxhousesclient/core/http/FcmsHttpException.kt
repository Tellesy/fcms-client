package ly.neptune.nexus.fcms.fxhousesclient.core.http

/**
 * HTTP exception for FCMS API non-2xx responses.
 */
class FcmsHttpException(
    val status: Int,
    val code: String?,
    override val message: String?,
    val body: String?,
    val headers: Map<String, String>,
    val retryAfterSeconds: Long? = null,
) : RuntimeException(message)
