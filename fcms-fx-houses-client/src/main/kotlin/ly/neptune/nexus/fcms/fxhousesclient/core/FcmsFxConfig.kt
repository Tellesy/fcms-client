package ly.neptune.nexus.fcms.fxhousesclient.core

import java.util.function.Supplier

/**
 * Configuration for FX Houses client (separate token from bank client).
 */
data class FcmsFxConfig @JvmOverloads constructor(
    val baseUrl: String,
    val tokenProvider: () -> String,
    val connectTimeoutMillis: Long = 5_000,
    val readTimeoutMillis: Long = 30_000,
    val writeTimeoutMillis: Long = 30_000,
    val maxRequests: Int = 64,
    val maxRequestsPerHost: Int = 32,
    val userAgent: String = "fcms-fx-houses-client/1.1.4",
    val enableRetries: Boolean = false,
    val maxRetries: Int = 3,
    val maxRetryDelayMillis: Long = 10_000,
) {
    @JvmOverloads
    constructor(
        baseUrl: String,
        tokenProvider: Supplier<String>,
        connectTimeoutMillis: Long = 5_000,
        readTimeoutMillis: Long = 30_000,
        writeTimeoutMillis: Long = 30_000,
        maxRequests: Int = 64,
        maxRequestsPerHost: Int = 32,
        userAgent: String = "fcms-fx-houses-client/1.1.0",
        enableRetries: Boolean = false,
        maxRetries: Int = 3,
        maxRetryDelayMillis: Long = 10_000,
    ) : this(
        baseUrl = baseUrl,
        tokenProvider = { tokenProvider.get() },
        connectTimeoutMillis = connectTimeoutMillis,
        readTimeoutMillis = readTimeoutMillis,
        writeTimeoutMillis = writeTimeoutMillis,
        maxRequests = maxRequests,
        maxRequestsPerHost = maxRequestsPerHost,
        userAgent = userAgent,
        enableRetries = enableRetries,
        maxRetries = maxRetries,
        maxRetryDelayMillis = maxRetryDelayMillis,
    )
}
