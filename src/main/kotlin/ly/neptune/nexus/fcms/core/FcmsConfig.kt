package ly.neptune.nexus.fcms.core

import java.util.function.Supplier

/**
 * SDK configuration for FCMS Salaries client.
 *
 * @property baseUrl Base URL of the FCMS Salaries API, e.g. "https://bank.example.com" (no trailing slash required).
 * @property tokenProvider Supplier of bearer token. Support for rotation per-call.
 * @property connectTimeoutMillis Connect timeout in milliseconds (default 5000).
 * @property readTimeoutMillis Read timeout in milliseconds (default 30000).
 * @property writeTimeoutMillis Write timeout in milliseconds (default 30000).
 * @property maxRequests OkHttp Dispatcher max concurrent requests.
 * @property maxRequestsPerHost OkHttp Dispatcher per-host max concurrent requests.
 * @property userAgent User-Agent header value sent with requests.
 * @property enableRetries Opt-in idempotent retry policy (GET only) including 429 Retry-After handling.
 * @property maxRetries Maximum number of retries when enabled.
 * @property maxRetryDelayMillis Upper bound for backoff delays when retrying.
 */
data class FcmsConfig @JvmOverloads constructor(
    val baseUrl: String,
    val tokenProvider: () -> String,
    val connectTimeoutMillis: Long = 5_000,
    val readTimeoutMillis: Long = 30_000,
    val writeTimeoutMillis: Long = 30_000,
    val maxRequests: Int = 64,
    val maxRequestsPerHost: Int = 32,
    val userAgent: String = "fcms-client/1.1.3",
    val enableRetries: Boolean = false,
    val maxRetries: Int = 3,
    val maxRetryDelayMillis: Long = 10_000,
) {
    /** Java-friendly constructor accepting a Supplier<String> for the token provider. */
    @JvmOverloads
    constructor(
        baseUrl: String,
        tokenProvider: Supplier<String>,
        connectTimeoutMillis: Long = 5_000,
        readTimeoutMillis: Long = 30_000,
        writeTimeoutMillis: Long = 30_000,
        maxRequests: Int = 64,
        maxRequestsPerHost: Int = 32,
        userAgent: String = "fcms-client/1.1.3",
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
