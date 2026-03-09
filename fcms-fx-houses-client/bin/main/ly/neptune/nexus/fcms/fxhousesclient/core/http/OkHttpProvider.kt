package ly.neptune.nexus.fcms.fxhousesclient.core.http

import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit

internal object OkHttpProvider {
    private const val MAX_IDLE_CONNECTIONS = 5
    private const val KEEP_ALIVE_SECONDS = 30L

    data class ManagedClient(
        val client: OkHttpClient,
        private val dispatcher: Dispatcher,
        private val connectionPool: ConnectionPool,
    ) : AutoCloseable {
        override fun close() {
            try {
                dispatcher.cancelAll()
            } catch (_: Exception) {
                // ignore
            }
            try {
                dispatcher.executorService.shutdown()
            } catch (_: Exception) {
                // ignore
            }
            try {
                connectionPool.evictAll()
            } catch (_: Exception) {
                // ignore
            }
        }
    }

    fun createManaged(config: FcmsFxConfig): ManagedClient {
        val dispatcher = Dispatcher().apply {
            maxRequests = config.maxRequests
            maxRequestsPerHost = config.maxRequestsPerHost
        }
        val connectionPool = ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS)

        val client = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectionPool(connectionPool)
            .connectTimeout(Duration.ofMillis(config.connectTimeoutMillis))
            .readTimeout(Duration.ofMillis(config.readTimeoutMillis))
            .writeTimeout(Duration.ofMillis(config.writeTimeoutMillis))
            .retryOnConnectionFailure(true)
            .addInterceptor(RequestIdInterceptor())
            .build()

        return ManagedClient(client, dispatcher, connectionPool)
    }
}
