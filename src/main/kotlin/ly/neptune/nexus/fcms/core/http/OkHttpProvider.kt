package ly.neptune.nexus.fcms.core.http

import ly.neptune.nexus.fcms.core.FcmsConfig
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
            // Cancel queued and running calls, then shutdown the executor to avoid JVM hanging.
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
    
    fun create(config: FcmsConfig): OkHttpClient {
        return createManaged(config).client
    }

    fun createManaged(config: FcmsConfig): ManagedClient {
        val dispatcher = Dispatcher().apply {
            maxRequests = config.maxRequests
            maxRequestsPerHost = config.maxRequestsPerHost
        }
        // Connection pool with shorter keep-alive to avoid stale connections
        val connectionPool = ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS)

        val client = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectionPool(connectionPool)
            .connectTimeout(Duration.ofMillis(config.connectTimeoutMillis))
            .readTimeout(Duration.ofMillis(config.readTimeoutMillis))
            .writeTimeout(Duration.ofMillis(config.writeTimeoutMillis))
            .retryOnConnectionFailure(true) // Retry on connection failures
            .addInterceptor(RequestIdInterceptor())
            .build()

        return ManagedClient(client, dispatcher, connectionPool)
    }
}
