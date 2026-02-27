# FCMS Clients Documentation (v1.3.3 & v1.1.3)

This document provides a comprehensive guide for using both the **Bank-Side SDK (`fcms-client` v1.3.3)** and the **FX Houses SDK (`fcms-fx-houses-client` v1.1.3)**. 

---

## 1. Overview & What's New

### 🏦 Bank-Side SDK (`fcms-client` v1.3.3)
The bank SDK is used by the central administration (e.g., the bank) to manage users, salaries, business/invoiced purchase requests, and to oversee FX houses.

**What's New in v1.3.3:**
- **FJx proCerr _counJSONcs ophsuyemapp modfrhi `b ekAcceanusttm field_ato `FxPuacchssqpodels. rmdels.Thi ensee me daisaprcorrectl.
-**Fix:** Aded mssing `contract` ild o`FxurheRequet` models

**What's New in v1.3.0 (Previous):**
- Added `listFxPurchaseRequests` endpoint in the `FcmsFxHousesClient` for the bank to list and filter purchase requests associated with FX Houses.
- Expanded filtering options for FX contracts (`FxContractsListFilter`) and FX purchase requests (`FxPurchaseRequestsListFilter`).
- Centralized token management via `FcmsConfig`.
- Exposed all endpoints in `FcmsFxHousesClientJava` for easy Java interop.

### 🏢 FX Houses SDK (`fcms-fx-houses-client` v1.1.3)
A **brand new, dedicated SDK** for FX houses (exchange companies). This SDK uses a separate authentication token bound directly to the FX house.

**What's New in v1.1.3:**
- **Fix:** Corrected JSON property mapping from `bankAccount` to `bank_account` on both `PurchaseRequestQueueItem` and `PurchaseRequest` models.

**What's New in v1.1.0 (Previous):**
- **Contracts:** Create and list FX contracts (`cash_price`, `bank_transfer_price`).
- **Purchase Requests:** List pending queue, show, list all, approve, decline, and process requests.
- **Bank Accounts:** List bank accounts via the newly added `FcmsFxBankAccountsClient`.
- **Miscs & Rates:** Fetch decline reasons, request states, and exchange rates.
- **Reports:** View purchase request state summaries.

---

## 2. Installation

### Gradle (Kotlin DSL)
```kotlin
repositories { mavenCentral() }

dependencies {
    // Bank SDK
    implementation("io.github.tellesy:fcms-client:1.3.6")
    
    // FX Houses SDK
    implementation("io.github.tellesy:fcms-fx-houses-client:1.1.4")
}
```

### Maven
```xml
<dependencies>
    <!-- Bank SDK -->
    <dependency>
        <groupId>io.github.tellesy</groupId>
        <artifactId>fcms-client</artifactId>
        <version>1.3.6</version>
    </dependency>

    <!-- FX Houses SDK -->
    <dependency>
        <groupId>io.github.tellesy</groupId>
        <artifactId>fcms-fx-houses-client</artifactId>
        <version>1.1.4</version>
    </dependency>
</dependencies>
```

---

## 3. Using the Bank-Side SDK (`fcms-client`)

### Configuration & Initialization

```kotlin
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.fxhouses.FcmsFxHousesClients

suspend fun main() {
    val config = FcmsConfig(
        baseUrl = System.getenv("FCMS_BASE_URL"),
        tokenProvider = { System.getenv("FCMS_BANK_TOKEN") }
    )

    val bankFxClient = FcmsFxHousesClients.create(config)
    
    // List all FX Houses
    val housesPage = bankFxClient.listFxHouses(page = 1)
    println(housesPage.data)

    // Close client on shutdown
    bankFxClient.close()
}
```

### Key Endpoints & Structures (Bank tracking FX Houses)

**List FX Contracts**
```kotlin
import ly.neptune.nexus.fcms.fxhouses.model.FxContractsListFilter

val contracts = bankFxClient.listContracts(
    page = 1,
    filter = FxContractsListFilter(state = "pending")
)
```

**List FX Purchase Requests (New in 1.3.0)**
```kotlin
import ly.neptune.nexus.fcms.fxhouses.model.FxPurchaseRequestsListFilter

val requests = bankFxClient.listFxPurchaseRequests(
    page = 1,
    filter = FxPurchaseRequestsListFilter(
        type = "cash",
        state = "approved"
    )
)
```
**Response Structure (`FxPurchaseRequest`):**
```json
{
  "uuid": "b09a74a3-a108-4450-b5f7-2d2f2901a59f",
  "reference": "FCPR-027-20260208-H5XAJ9638JLV",
  "amount_requested": "2000",
  "type": { "code": 2, "name": "حوالة سريعة" },
  "state": { "code": "approved", "name": "تم الموافقة" },
  "company": {
    "uuid": "434a3e0a-3644-4d4a-a0b9-4cc22efb0b09",
    "name": "شركة الواحات للصرافة والخدمات المالية",
    "cbl_key": "CBL5-128804"
  },
  "bankAccount": {
    "uuid": "5136514f-10f9-41ed-a9c6-61ea24d7b117",
    "iban": "LY97027102102100541801014",
    "user": {
       "first_name": "فارس",
       "nid": "119900254979"
    }
  }
}
```

**Reports (New in 1.3.5)**
View aggregated counts and totals of purchase requests by state.
```kotlin
import ly.neptune.nexus.fcms.reports.FcmsReportsClients

val reportsClient = FcmsReportsClients.create(config)

val summary = reportsClient.purchaseRequestsStatesSummary(
    approvedOn = "2026-01-01",
    type = "card"
)
```
**Response Structure (`PurchaseRequestsStatesSummary`):**
```json
{
    "approved": {
        "count": 16,
        "total": 49900
    },
    "processed": {
        "count": 79268,
        "total": 259060400
    },
    "declined": {
        "count": 9244,
        "total": 28191400
    }
}
```

**Exchange Rates (Updated in 1.3.6)**
List paginated exchange rates with optional date filtering.
```kotlin
import ly.neptune.nexus.fcms.miscs.FcmsMiscsClients

val miscsClient = FcmsMiscsClients.create(config)

val ratesPage = miscsClient.listExchangeRates(
    page = 1,
    filterDate = "2024-01-31"
)
```
**Response Structure (`Page<ExchangeRate>`):**
```json
{
    "date": "2024-02-06",
    "rate": "4.8556"
}
```

---

## 4. Using the FX Houses SDK (`fcms-fx-houses-client`)

This SDK is for the exchange companies themselves. It uses `FcmsFxConfig` and requires an FX-specific token.

### Configuration & Initialization

```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.requests.FcmsFxRequestsClients

suspend fun main() {
    val config = FcmsFxConfig(
        baseUrl = System.getenv("FCMS_BASE_URL"),
        tokenProvider = { System.getenv("FCMS_FX_TOKEN") }
    )

    val requestsClient = FcmsFxRequestsClients.create(config)
    // ... use client ...
    requestsClient.close()
}
```

### Key Endpoints & Structures

#### 1. Contracts
Create a contract with cash and bank transfer prices.
```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.contracts.FcmsFxContractsClients
import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContractCreateRequest

val contractsClient = FcmsFxContractsClients.create(config)

val response = contractsClient.create(
    FxContractCreateRequest(
        cashPrice = "5.74",
        bankTransferPrice = "5.84",
        amount = "10000000"
    )
)
```
**Response Structure:**
```json
{
  "uuid": "660e8400-e29b-41d4-a716-446655440001",
  "amount": 10000000,
  "cash_price": 5.74,
  "bank_transfer_price": 5.84,
  "state": { "code": "pending", "name": "قيد الانتظار" }
}
```

#### 2. Purchase Requests (Queue & Actions)
List pending requests and approve/decline them.
```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestActionRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestProcessRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestDeclineRequest

val queue = requestsClient.listPendingRequests(page = 1)
val firstUuid = queue.data.first().uuid!!

// Approve
requestsClient.approvePurchaseRequest(
    firstUuid, 
    PurchaseRequestActionRequest(ts = System.currentTimeMillis() / 1000)
)

// Process (cash or bank)
requestsClient.processPurchaseRequest(
    firstUuid, 
    PurchaseRequestProcessRequest(
        ts = System.currentTimeMillis() / 1000, 
        type = "cash"
    )
)

// Decline
requestsClient.declinePurchaseRequest(
    firstUuid,
    PurchaseRequestDeclineRequest(
        ts = System.currentTimeMillis() / 1000,
        declineReason = "invalid_documents",
        declineReasonNote = "Expired passport"
    )
)
```

#### 3. Bank Accounts (New in 1.1.0)
List bank accounts linked to the FX House.
```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.accounts.FcmsFxBankAccountsClients

val accountsClient = FcmsFxBankAccountsClients.create(config)
val accounts = accountsClient.listBankAccounts(page = 1)
```

#### 4. Reports & Miscs
View summaries and exchange rates.
```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.reports.FcmsFxReportsClients
import ly.neptune.nexus.fcms.fxhousesclient.miscs.FcmsFxMiscsClients

val reportsClient = FcmsFxReportsClients.create(config)
val summary = reportsClient.purchaseRequestsStatesSummary(
    approvedOn = "2026-02-01", 
    type = "cash"
)

val miscsClient = FcmsFxMiscsClients.create(config)
val rates = miscsClient.listExchangeRates(dateFilter = "2026-02-02")
```
**Reports Response Structure:**
```json
{
  "approved": { "count": 45, "total": 225000.0 },
  "processed": { "count": 38, "total": 190000.0 },
  "declined": { "count": 7, "total": 35000.0 }
}
```

---

## 5. Java Interoperability

Both SDKs provide Java-friendly wrappers using `CompletableFuture`. Simply append `Java` to the client name.

```java
import ly.neptune.nexus.fcms.core.FcmsConfig;
import ly.neptune.nexus.fcms.fxhouses.FcmsFxHousesClientJava;
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig;
import ly.neptune.nexus.fcms.fxhousesclient.requests.FcmsFxRequestsClientJava;

public class Main {
    public static void main(String[] args) throws Exception {
        // Bank SDK Java Client
        FcmsConfig bankConfig = new FcmsConfig("https://api.example.com", () -> "BANK_TOKEN");
        try (FcmsFxHousesClientJava bankFxClient = FcmsFxHousesClientJava.create(bankConfig)) {
            bankFxClient.listFxHouses(1, null).thenAccept(page -> {
                System.out.println(page.getData());
            }).join();
        }

        // FX Houses SDK Java Client
        FcmsFxConfig fxConfig = new FcmsFxConfig("https://api.example.com", () -> "FX_TOKEN");
        try (FcmsFxRequestsClientJava requestsClient = FcmsFxRequestsClientJava.create(fxConfig)) {
            requestsClient.listPendingRequests(1, null).thenAccept(queue -> {
                System.out.println(queue.getData());
            }).join();
        }
    }
}
```

## Best Practices
1. **Always call `close()`**: OkHttp dispatchers and coroutine scopes need to be shut down properly to prevent memory leaks and JVM hanging.
2. **Token Rotation**: The `tokenProvider` is executed on every HTTP call. You can safely rotate your tokens dynamically by having the supplier fetch from a cache or database.
3. **Use the correct client**: Do not attempt to use the bank token in the `fcms-fx-houses-client`. They are logically and securely isolated on the server.
