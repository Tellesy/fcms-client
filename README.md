# FCMS Client (Kotlin/Java SDK)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.tellesy/fcms-client.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.tellesy/fcms-client)

A small, dependency-light JVM SDK for FCMS APIs (Salaries, Accounts, Requests, Business Purchase Requests, Invoiced Purchase Requests, Miscs, Reports, FX Houses). Works in any Kotlin or Java app (no Spring dependency). Optimized for performance and correctness.

- Group: `io.github.tellesy`
- Artifact: `fcms-client`
- Version: `1.2.0`
- JVM: Java 21+

## What's New in 1.2.0

- Full API coverage for the published Postman collection: Accounts, Requests, Business Purchase Requests, Invoiced Purchase Requests, Miscs, Reports, FX Houses.
- Java `CompletableFuture` facades added for all client modules.
- Strong shutdown semantics: all clients implement `close()` to cancel in-flight calls and shutdown OkHttp resources (safe for Spring Boot shutdown).

## Supported Endpoints

- GET `{baseUrl}/api/v1/mof/transactions` → `Page<Transaction>`
- GET `{baseUrl}/api/v1/mof/transactions/{uuid}` → `Transaction`
- POST `{baseUrl}/api/v1/mof/transactions/{uuid}/complete` → `Transaction`
- POST `{baseUrl}/api/v1/mof/transactions/{uuid}/reject` → `Transaction`
- GET `{baseUrl}/api/v1/misc/mof/rejection-reasons` → `List<RejectionReason>`

- GET `{baseUrl}/api/v1/purchase-requests` → `Page<PurchaseRequest>`
- GET `{baseUrl}/api/v1/purchase-requests/{uuid}` → `PurchaseRequest`
- PATCH `{baseUrl}/api/v1/purchase-requests/{uuid}/approve` → `PurchaseRequest`
- PATCH `{baseUrl}/api/v1/purchase-requests/{uuid}/decline` → `PurchaseRequest`
- PATCH `{baseUrl}/api/v1/purchase-requests/{uuid}/process` → `PurchaseRequest`
- GET `{baseUrl}/api/v1/deleted-purchase-requests` → `Page<DeletedPurchaseRequest>`
- POST `{baseUrl}/api/v1/refresh-queue` → `RefreshQueueResult`

- GET `{baseUrl}/api/v1/bank-accounts` → `Page<BankAccount>` (supports filters via `AccountsListFilter`)
- PATCH `{baseUrl}/api/v1/bank-accounts/{uuid}/match` → `BankAccount`
- PATCH `{baseUrl}/api/v1/bank-accounts/{uuid}/reject` → `BankAccount`
- PATCH `{baseUrl}/api/v1/bank-accounts/{uuid}/unreject` → `BankAccount`
- PATCH `{baseUrl}/api/v1/bank-accounts/{uuid}/update` → `BankAccount`

- GET `{baseUrl}/api/v1/purchase-requests-queue` → `Page<PurchaseRequestQueueItem>`

- Business Purchase Requests
  - POST `{baseUrl}/api/v1/business-purchase-requests/check` → `BusinessPurchaseRequest`
  - GET `{baseUrl}/api/v1/business-purchase-requests` → `Page<BusinessPurchaseRequest>`
  - GET `{baseUrl}/api/v1/business-purchase-requests/{uuid}` → `BusinessPurchaseRequest`
  - POST `{baseUrl}/api/v1/business-purchase-requests` → `BusinessPurchaseRequest`
  - POST `{baseUrl}/api/v1/business-purchase-requests/{uuid}/close` → `BusinessPurchaseRequest` (multipart)
  - PATCH `{baseUrl}/api/v1/business-purchase-requests/{uuid}/process` → `BusinessPurchaseRequest`

- Invoiced Purchase Requests
  - POST `{baseUrl}/api/v1/invoiced-purchase-requests` → `InvoicedPurchaseRequest` (multipart)
  - GET `{baseUrl}/api/v1/invoiced-purchase-requests` → `Page<InvoicedPurchaseRequest>`
  - GET `{baseUrl}/api/v1/invoiced-purchase-requests/{uuid}` → `InvoicedPurchaseRequest`
  - PATCH `{baseUrl}/api/v1/invoiced-purchase-requests/{uuid}/process` → `InvoicedPurchaseRequest`

- Miscs (lookups)
  - GET `{baseUrl}/api/v1/miscs/bank-account-rejection-reasons` → `List<CodeName>`
  - GET `{baseUrl}/api/v1/miscs/business-purchase-request-states` → `List<CodeName>`
  - GET `{baseUrl}/api/v1/miscs/bank-branches` → `List<BankBranch>`
  - GET `{baseUrl}/api/v1/miscs/business-activities` → `List<CodeName>`
  - GET `{baseUrl}/api/v1/miscs/countries` → `List<CodeName>`
  - GET `{baseUrl}/api/v1/miscs/currencies` → `List<CodeName>`
  - GET `{baseUrl}/api/v1/miscs/exchange-rates` → `List<ExchangeRate>`
  - GET `{baseUrl}/api/v1/miscs/invoiced-purchase-request-states` → `List<CodeName>`
  - GET `{baseUrl}/api/v1/miscs/decline-reasons` → `List<CodeName>`
  - GET `{baseUrl}/api/v1/miscs/purchase-request-states` → `List<CodeName>`
  - GET `{baseUrl}/api/v1/miscs/purchase-request-types` → `List<CodeName>`
  - GET `{baseUrl}/api/v1/miscs/bank-account-states` → `List<CodeName>`
  - GET `{baseUrl}/api/v1/miscs/contract-reject-reasons` → `List<CodeName>`

- Reports
  - GET `{baseUrl}/api/v1/reports/purchase-requests-states` → `List<PurchaseRequestsStatesReportRow>`

- FX Houses
  - GET `{baseUrl}/api/v1/fx-houses` → `Page<FxHouse>`
  - GET `{baseUrl}/api/v1/fx-houses/contracts` → `Page<FxContract>`
  - PATCH `{baseUrl}/api/v1/fx-houses/contracts/{uuid}/approve` → `FxContract`
  - PATCH `{baseUrl}/api/v1/fx-houses/contracts/{uuid}/process` → `FxContract`
  - PATCH `{baseUrl}/api/v1/fx-houses/contracts/{uuid}/decline` → `FxContract`

JSON is automatically unwrapped from envelopes like `{ "data": ... }`. Pagination is resilient to both Laravel shapes: root `links` object/array and `meta.links` arrays.

## Add Dependency

Gradle (Kotlin DSL):
```kotlin
repositories { mavenCentral() }
dependencies { implementation("io.github.tellesy:fcms-client:1.2.0") }
```

Maven:
```xml
<dependency>
  <groupId>io.github.tellesy</groupId>
  <artifactId>fcms-client</artifactId>
  <version>1.2.0</version>
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
    val miscs = ly.neptune.nexus.fcms.miscs.FcmsMiscsClients.create(config)
    val fx = ly.neptune.nexus.fcms.fxhouses.FcmsFxHousesClients.create(config)

    // Salaries
    val page1 = salaries.listTransactions(page = 1)
    // Typed filtering
    val pending2025 = salaries.listTransactionsFiltered(
        page = 1,
        filter = SalariesListFilter(state = "pending", year = 2025)
    )
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

    // FX Houses
    val fxHouses = fx.listFxHouses(page = 1)
    val contracts = fx.listContracts(page = 1)
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
      // Typed filtering from Java
      Page<Transaction> filtered = client
        .listTransactions(1, new SalariesListFilter("pending", 2025, null), null)
        .get();
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

## Query parameters and filtering

The SDK builds URLs and query strings for you. Here’s how to pass filters safely:

- Salaries `GET {baseUrl}/api/v1/mof/transactions`
  - Use `SalariesListFilter` with `listTransactionsFiltered(...)` to set Laravel-style filters:

Kotlin:
```kotlin
val salariesPage = salaries.listTransactionsFiltered(
    page = 1,
    filter = SalariesListFilter(
        state = "pending",   // -> filter[state]=pending
        year = 2025,          // -> filter[year]=2025
        month = 8             // -> filter[month]=8
    )
)

// Convenience helpers
val byState = salaries.listTransactionsByState("pending", page = 1)
val byYear = salaries.listTransactionsByYear(2025, page = 1)
val byYearMonth = salaries.listTransactionsByYearMonth(2025, 8, page = 1)
val byAll = salaries.listTransactionsByYearMonthState(2025, 8, "completed", page = 1)

// Raw map (advanced)
val raw = salaries.listTransactionsWithFilters(
    page = 1,
    filters = mapOf(
        "filter[state]" to "pending",
        "filter[year]" to "2025"
    )
)
```

Java:
```java
// Typed filter
Page<Transaction> page = client
  .listTransactions(1, new SalariesListFilter("pending", 2025, 8), null)
  .get();

// Convenience wrappers
Page<Transaction> byState = client.listTransactionsByState("pending", 1, null).get();
Page<Transaction> byYear = client.listTransactionsByYear(2025, 1, null).get();
Page<Transaction> byYearMonth = client.listTransactionsByYearMonth(2025, 8, 1, null).get();
Page<Transaction> byAll = client.listTransactionsByYearMonthState(2025, 8, "completed", 1, null).get();

// Raw map
Page<Transaction> raw = client
  .listTransactionsWithFilters(1, Map.of("filter[state]", "pending", "filter[year]", "2025"), null)
  .get();
```

- Accounts `GET {baseUrl}/api/v1/bank-accounts`
  - Use `AccountsListFilter` to set query params that map to Laravel-style filter keys:

Kotlin:
```kotlin
val accountsPage = accounts.listAccounts(
    page = 1,
    filter = AccountsListFilter(
        state = "pending",               // -> filter[state]=pending
        iban = "SA123...",               // -> filter[iban]=SA123...
        createdOn = "2025-01-15",        // -> filter[created_on]=2025-01-15
        approvedOn = null,                // omitted when null/blank
        rejectedOn = null,
        unrejectedOn = null,
        accountNumber = null,             // -> filter[account_number]=...
        hasAccountNumber = true           // -> filter[has_account_number]=true
    )
)
```

Java:
```java
AccountsListFilter filter = new AccountsListFilter(
    "pending",      // state -> filter[state]=pending
    null,            // iban
    null,            // createdOn (yyyy-MM-dd)
    null,            // approvedOn
    null,            // rejectedOn
    null,            // unrejectedOn
    null,            // accountNumber
    Boolean.TRUE     // hasAccountNumber -> filter[has_account_number]=true
);
Page<BankAccount> page = accountsClient.listAccounts(1, filter, null);
```

Notes:
- Only one question mark is used in a URL. Example with multiple filters: `...?filter[state]=pending&filter[year]=2025` (not `&?filter[year]=...`).
- Null/blank fields are omitted from the query string automatically.

## Salaries Data Models

The SDK maps Salaries API JSON into typed models. New fields added in 1.0.3 are marked; nullability updates in 1.0.4 are noted.

- Transaction
  - `uuid: String`
  - `state: String`
  - `individual: Individual`
  - `bankAccount: BankAccount`
  - `salary: Salary`
  - `entity: Entity?` (new in 1.0.3)
  - `description: String?` (new in 1.0.3)

- Individual
  - `name: String`, `nid: String`, `mofFinancialNumber: String`, `phoneNumber: String?`

- BankAccount
  - `number: String`, `iban: String?`, `bankBranch: String?` (new in 1.0.3)

- Salary
  - `amount: BigDecimal` (string or numeric in JSON supported)
  - `currency: String`
  - `period: Period { year: String, month: String }`

- Entity (new in 1.0.3)
  - `name: String?` (nullable since 1.0.4), `region: String?`

Example (truncated):
```json
{
  "uuid": "...",
  "state": "pending",
  "individual": { "name": "...", "nid": "...", "mofFinancialNumber": "..." },
  "bankAccount": { "number": "...", "iban": "...", "bankBranch": "..." },
  "salary": { "amount": "81834", "currency": "SAR", "period": { "year": "2025", "month": "08" } },
  "entity": { "name": "...", "region": "..." },
  "description": "..."
}
```

## Threading and Cleanup

- Each client instance owns its OkHttp dispatcher + connection pool.
- Suspend APIs are non-blocking; the Java facades use `CompletableFuture`.
- Always call `close()` to shutdown OkHttp resources; this prevents Spring Boot from hanging on shutdown.

### Spring Boot / long-running services

Recommended: create one singleton client per module and close it at shutdown.

- Kotlin (Spring)

```kotlin
@Configuration
class FcmsClientsConfig {
  @Bean(destroyMethod = "close")
  fun fcmsFxHousesClient(config: FcmsConfig) = ly.neptune.nexus.fcms.fxhouses.FcmsFxHousesClients.create(config)
}
```

- Java (Spring)

```java
@Bean(destroyMethod = "close")
public FcmsFxHousesClientJava fcmsFx(FcmsConfig config) {
  return FcmsFxHousesClientJava.create(config);
}
```

## Build, Test, Docs

- Build & test: `./gradlew clean test`
- Generate docs: `./gradlew dokkaHtml` (output at `build/dokka/html`)
- Publish to Maven Local: `./gradlew publishToMavenLocal`

## Versioning

Semantic Versioning (SemVer). Breaking changes bump the major version.

## Examples

Quick usage examples are provided in the Quick Start (Kotlin/Java) sections above.

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
    implementation("io.github.tellesy:fcms-client:1.2.0")
}
```

## FX Houses (شركات ومكاتب الصرافة) — Business Flow & Accounting Notes

### Overview

We (the bank) integrate with the Central Bank (CBL). Every day, FX houses submit **USD purchase contracts**.

Each contract includes:

- `amount` (USD) e.g. `"50000.00"`
- `cash_price` exchange rate for cash settlements
- `bank_transfer_price` exchange rate for bank transfers

### Daily FX Houses sync

Use `GET /api/v1/fx-houses` to fetch the list of FX houses linked to the bank:

- The list can **increase or decrease daily**.
- The consuming app should **cache/persist** this list and update it daily.

### Contract lifecycle

1. List new contracts: `GET /api/v1/fx-houses/contracts`
2. Decide to approve/decline based on compliance and available funds
3. End-of-day: process the contract and finalize amounts

### Important accounting rule (very important)

When the bank processes a contract, it must reserve funds using the **higher** of the two exchange rates:

```
effective_rate = max(cash_price, bank_transfer_price)
reserved_lyd = amount_usd * effective_rate
```

This is the amount to be held from the FX house account (LYD) when approving/processing.

### End-of-day settlement

At the end of the day, the contract is processed based on:

- `sum_of_approved_cash_price`
- `sum_of_approved_bank_transfer_price`

These represent what was actually approved and settled under each channel.

Any remaining reserved LYD (based on morning prices) should be returned to the FX house account.

### Timestamp

`ts` is the timestamp (Unix seconds) like the other APIs.

### Mermaid flow diagram

```mermaid
flowchart TD
  A[Start of day] --> B[Fetch FX houses list\nGET /api/v1/fx-houses]
  B --> C[Fetch contracts\nGET /api/v1/fx-houses/contracts]
  C --> D{Compliance + sufficient funds?}
  D -- No --> E[Decline contract\nPATCH /contracts/{uuid}/decline\n(ts, reason)]
  D -- Yes --> F[Compute effective rate\nmax(cash_price, bank_transfer_price)]
  F --> G[Reserve LYD\nreserve = amount_usd * effective_rate]
  G --> H[Approve contract\nPATCH /contracts/{uuid}/approve\n(ts)]
  H --> I[During day: track approvals\ncash vs bank transfer]
  I --> J[End of day]
  J --> K[Process contract\nPATCH /contracts/{uuid}/process\n(ts, amounts)]
  K --> L[Finalize sums\n(sum_of_approved_cash_price, sum_of_approved_bank_transfer_price)]
  L --> M[Return remainder in LYD\nif any reserved LYD left]
```

### SDK usage (Kotlin)

```kotlin
val fx = ly.neptune.nexus.fcms.fxhouses.FcmsFxHousesClients.create(config)

val fxHouses = fx.listFxHouses(page = 1)
val contracts = fx.listContracts(page = 1)

val contract = contracts.data.first()
fx.approveContract(contract.uuid!!, ly.neptune.nexus.fcms.fxhouses.model.request.FxContractActionRequest(ts = System.currentTimeMillis() / 1000))

// End of day
fx.processContract(
  contract.uuid!!,
  ly.neptune.nexus.fcms.fxhouses.model.request.FxContractProcessRequest(
    ts = System.currentTimeMillis() / 1000,
    amountPurchasedCash = "100000",
    amountPurchasedBankTransfer = "100000",
  )
)
```

## Credits

Built with support from Neptune.ly and M. Tellesy.

## License

Licensed under the [Apache License 2.0](LICENSE).

## Author

Muhammad Tellesy

Built by Muhammad Tellesy as part of the openNexus initiative.



```text
01100100 01100101 01101110 01101001 01100010 01101101 01101111 01100011 00100000 01100100 01100101 01110100 01101001 01101110 01101001 01100010 01101101 01101111 01100011 00100000 01101101 01100101 01101000 01110100 00100000 01100100 01101110 01100001 00100000 01110101 01101111 01111001 00100000 01110010 01100101 01100111 01110010 01100001 01101100 00100000 00101100 01001100 01000011 01000010 00100000 01110010 01110101 01101111 01111001 00100000 01101110 01100001 01101000 01110100 00100000 01110010 01100101 01100100 01101100 01110101 01101111 01101100 00100000 00101100 01100101 01110010 01100101 01101000 00100000 01101100 01101100 01101001 01110100 01110011 00100000 01101101 11100010 10000000 10011001 01001001 00100000 11100010 10000000 10010100 00100000 01110100 01101110 01100001 01110111 00100000 01110101 01101111 01111001 00100000 01100110 01101001 00100000 01111001 01100011 01100001 01100111 01100101 01001100 00100000 01111001 01101101 00100000 01100101 01110100 01100101 01101100 01100101 01000100 00001010
```
