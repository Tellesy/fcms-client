# FCMS Client (Kotlin/Java SDK)

A small, dependency-light JVM SDK for FCMS APIs (Salaries, Accounts, Requests). Works in any Kotlin or Java app (no Spring dependency). Optimized for performance and correctness.

- Group: `io.github.tellesy`
- Artifact: `fcms-client`
- Version: `1.0.0`
- JVM: Java 21+

## Supported Endpoints

- GET `{baseUrl}/api/v1/mof/transactions` → `Page<Transaction>`
- GET `{baseUrl}/api/v1/mof/transactions/{uuid}` → `Transaction`
- POST `{baseUrl}/api/v1/mof/transactions/{uuid}/complete` → `Transaction`
- POST `{baseUrl}/api/v1/mof/transactions/{uuid}/reject` → `Transaction`
- GET `{baseUrl}/api/v1/misc/mof/rejection-reasons` → `List<RejectionReason>`

- GET `{baseUrl}/api/v1/bank-accounts` → `Page<BankAccount>` (supports filters via `AccountsListFilter`)
- PATCH `{baseUrl}/api/v1/bank-accounts/{uuid}/match` → `BankAccount`
- PATCH `{baseUrl}/api/v1/bank-accounts/{uuid}/reject` → `BankAccount`
- PATCH `{baseUrl}/api/v1/bank-accounts/{uuid}/unreject` → `BankAccount`
- PATCH `{baseUrl}/api/v1/bank-accounts/{uuid}/update` → `BankAccount`

- GET `{baseUrl}/api/v1/purchase-requests-queue` → `Page<PurchaseRequestQueueItem>`

JSON is automatically unwrapped from envelopes like `{ "data": ... }`. Pagination is resilient to both Laravel shapes: root `links` object/array and `meta.links` arrays.

## Add Dependency

Gradle (Kotlin DSL):
```kotlin
repositories { mavenCentral() }
dependencies { implementation("io.github.tellesy:fcms-client:1.0.0") }
```

Maven:
```xml
<dependency>
  <groupId>io.github.tellesy</groupId>
  <artifactId>fcms-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Quick Start

Kotlin:
```kotlin
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.salaries.*
import ly.neptune.nexus.fcms.salaries.model.request.CompleteTransactionRequest
import ly.neptune.nexus.fcms.accounts.*
import ly.neptune.nexus.fcms.requests.*

suspend fun main() {
    val config = FcmsConfig(
        baseUrl = System.getenv("FCMS_BASE_URL"),
        tokenProvider = { System.getenv("FCMS_TOKEN") }
    )

    val salaries = FcmsSalariesClients.create(config)
    val accounts = FcmsAccountsClients.create(config)
    val requests = FcmsRequestsClients.create(config)

    // Salaries
    val page1 = salaries.listTransactions(page = 1)
    val tx = salaries.showTransaction(
        uuid = "8bb8fbde-21d7-4a37-99eb-fdce5294a1ee",
        options = RequestOptions(
            baseUrlOverride = "https://other-bank.example.com",
            tokenOverride = "Bearer <other token>"
        )
    )
    salaries.completeTransaction(
        uuid = "569a715c-1053-4be7-acff-b65a8d915724",
        request = CompleteTransactionRequest("BANK-REF-123", "1724232056")
    )

    // Accounts
    val accountsPage = accounts.listAccounts(
        page = 1,
        filter = AccountsListFilter(state = "pending")
    )
    // Requests (pending purchase requests queue)
    val queuePage = requests.listPendingRequests(page = 1)
}
```

Java:
```java
import ly.neptune.nexus.fcms.core.FcmsConfig;
import ly.neptune.nexus.fcms.core.RequestOptions;
import ly.neptune.nexus.fcms.salaries.*;
import ly.neptune.nexus.fcms.salaries.model.Page;
import ly.neptune.nexus.fcms.salaries.model.Transaction;
import ly.neptune.nexus.fcms.salaries.model.request.CompleteTransactionRequest;

public class Example {
  public static void main(String[] args) throws Exception {
    FcmsConfig config = new FcmsConfig(
      System.getenv("FCMS_BASE_URL"),
      () -> System.getenv("FCMS_TOKEN")
    );
    try (FcmsSalariesClientJava client = FcmsSalariesClientJava.create(config)) {
      Page<Transaction> page = client.listTransactions(1, null).get();
      Transaction t = client.showTransaction("8bb8fbde-21d7-4a37-99eb-fdce5294a1ee", null).get();
    }
  }
}
```

## Configuration

`FcmsConfig` sets global client defaults. `RequestOptions` allows per-call overrides:

- Base URL: `FcmsConfig.baseUrl` (override via `RequestOptions.baseUrlOverride`)
- Token: `FcmsConfig.tokenProvider` (override via `RequestOptions.tokenOverride`)
- Timeouts: connect/read/write timeouts globally; per-call read timeout via `RequestOptions.readTimeoutMillisOverride`
- Dispatcher concurrency: `maxRequests`, `maxRequestsPerHost`
- Retries: opt-in via `enableRetries`; only idempotent GETs retry by default; 429 Retry-After respected

## Error Handling

Non-2xx responses throw `ly.neptune.nexus.fcms.core.http.FcmsHttpException` with:

- `status` (HTTP)
- `code` and `message` (if available from body)
- `body` (raw)
- `headers` (map) and `retryAfterSeconds` (parsed if present)

## Pagination

List APIs return `Page<T>` with fields: `data`, `total`, `perPage`, `currentPage`, `next`, `prev`. Examples:

- Salaries: `listTransactions(page)` → `Page<Transaction>`
- Accounts: `listAccounts(page, filter)` → `Page<BankAccount>`
- Requests: `listPendingRequests(page)` → `Page<PurchaseRequestQueueItem>`

- Laravel root links object `{ links: { next, prev } }` supported
- Laravel `meta.links` array supported

## Threading and Cleanup

- The client uses a single shared OkHttp `OkHttpClient` with HTTP/2, connection pooling, gzip.
- Suspend APIs are non-blocking; the Java facade uses `CompletableFuture`.
- Call `close()` when finished to allow resources to be released by GC.

## Build, Test, Docs

- Build & test: `./gradlew clean test`
- Generate docs: `./gradlew dokkaHtml` (output at `build/dokka/html`)
- Publish to Maven Local: `./gradlew publishToMavenLocal`

## Versioning

Semantic Versioning (SemVer). Breaking changes bump the major version.

## Examples

See `examples/` for runnable snippets.

## Security

- Do not hardcode tokens or base URLs. Inject them via environment variables or your secret manager.
- Tests should use ephemeral tokens and never commit real credentials.
- Use per-call overrides with `RequestOptions` for multi-tenant scenarios instead of creating many clients.

## Using from Local Maven (for development)

If the artifact isn't in your remote repository yet, you can consume it locally:

1. In this repo: `./gradlew publishToMavenLocal`
2. In your app's Gradle config, add `mavenLocal()` before `mavenCentral()`:

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}
dependencies {
    implementation("io.github.tellesy:fcms-client:1.0.0")
}
```

## Author

Muhammad Tellesy



```text
01100100 01100101 01101110 01101001 01100010 01101101 01101111 01100011 00100000 01100100 01100101 01110100 01101001 01101110 01101001 01100010 01101101 01101111 01100011 00100000 01101101 01100101 01101000 01110100 00100000 01100100 01101110 01100001 00100000 01110101 01101111 01111001 00100000 01110010 01100101 01100111 01110010 01100001 01101100 00100000 00101100 01001100 01000011 01000010 00100000 01110010 01110101 01101111 01111001 00100000 01101110 01100001 01101000 01110100 00100000 01110010 01100101 01100100 01101100 01110101 01101111 01101100 00100000 00101100 01100101 01110010 01100101 01101000 00100000 01101100 01101100 01101001 01110100 01110011 00100000 01101101 11100010 10000000 10011001 01001001 00100000 11100010 10000000 10010100 00100000 01110100 01101110 01100001 01110111 00100000 01110101 01101111 01111001 00100000 01100110 01101001 00100000 01111001 01100011 01100001 01100111 01100101 01001100 00100000 01111001 01101101 00100000 01100101 01110100 01100101 01101100 01100101 01000100 00001010
```
