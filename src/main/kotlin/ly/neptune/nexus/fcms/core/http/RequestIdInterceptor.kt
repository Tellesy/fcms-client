package ly.neptune.nexus.fcms.core.http

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Application interceptor that adds headers and supports a per-request read-timeout override via header.
 *
 * Supported headers:
 * - X-Request-Id: generated if absent
 * - X-Read-Timeout-Millis: optional read timeout override for this call
 */
internal class RequestIdInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val rid = request.header("X-Request-Id") ?: UUID.randomUUID().toString()
        val builder = request.newBuilder()
            .header("X-Request-Id", rid)
            .header("Accept", "application/json")
        val readOverride = request.header("X-Read-Timeout-Millis")
        if (readOverride != null) {
            val millis = readOverride.toLongOrNull()
            if (millis != null && millis > 0) {
                // Remove header before sending downstream
                builder.removeHeader("X-Read-Timeout-Millis")
                request = builder.build()
                return chain.withReadTimeout(millis.toInt(), TimeUnit.MILLISECONDS).proceed(request)
            }
        }
        request = builder.build()
        return chain.proceed(request)
    }
}
