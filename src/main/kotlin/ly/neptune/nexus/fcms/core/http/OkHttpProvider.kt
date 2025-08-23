package ly.neptune.nexus.fcms.core.http

import ly.neptune.nexus.fcms.core.FcmsConfig
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.time.Duration

internal object OkHttpProvider {
    fun create(config: FcmsConfig): OkHttpClient {
        val dispatcher = Dispatcher().apply {
            maxRequests = config.maxRequests
            maxRequestsPerHost = config.maxRequestsPerHost
        }
        return OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectTimeout(Duration.ofMillis(config.connectTimeoutMillis))
            .readTimeout(Duration.ofMillis(config.readTimeoutMillis))
            .writeTimeout(Duration.ofMillis(config.writeTimeoutMillis))
            .addInterceptor(RequestIdInterceptor())
            .build()
    }
}
