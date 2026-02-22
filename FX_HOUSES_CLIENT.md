# FCMS FX Houses Client – New Changes & Usage

This document summarizes the **latest changes** and explains how to use the new **`fcms-fx-houses-client`** SDK, including how to attach an FX house token and update your dependency versions.

---

## 1) What’s New

### ✅ New FX Houses Client (v1.1.0)
A dedicated SDK for **FX houses (companies)** with their own authentication token, separate from the bank client.

**Included modules:**
- Contracts
- Purchase Requests (queue, list, approve/decline/process, deleted)
- Miscs (decline reasons, request types/states, exchange rates)
- Exchange Rates (paginated)
- Reports (purchase requests states summary)

### ✅ Lifecycle Safety
All clients provide a `close()` to fully shut down OkHttp and cancel coroutine scopes. This avoids JVM hangs in long‑running services.

### ✅ Publish Targets
The new client is published as:

- **Group:** `io.github.tellesy`
- **Artifact:** `fcms-fx-houses-client`
- **Version:** `1.1.0`

---

## 2) Add the FX Houses Client Dependency

### Gradle (Kotlin DSL)
```kotlin
repositories { mavenCentral() }

dependencies {
    implementation("io.github.tellesy:fcms-fx-houses-client:1.1.0")
}
```

### Maven
```xml
<dependency>
  <groupId>io.github.tellesy</groupId>
  <artifactId>fcms-fx-houses-client</artifactId>
  <version>1.1.0</version>
</dependency>
```

---

## 3) How to Attach an FX House Token

FX houses **do not use** the bank token. They require a **separate FX house token**.

### How token linking works
1. **FCMS Admin creates the FX House** in the system.
2. Admin **generates a token** for that FX House.
3. That token is **server‑linked to the FX House account** automatically.
4. You only need to supply that token in your client config.

> ✅ No extra “link” call is required from the SDK. The token itself is already bound to the FX house on the server.

### Recommended setup (environment variable)
```bash
export FCMS_FX_TOKEN="<fx-house-token>"
export FCMS_BASE_URL="https://fcms.example.com"
```

---

## 4) API Reference (FX Houses)

All endpoints below are called under:

```
{baseUrl}/api/v1/...
```

### Contracts
- `POST /contracts` → create FX contract (cash + bank transfer price)
- `GET /contracts` → list contracts (paginated)

### Purchase Requests
- `GET /purchase-requests-queue` → list pending queue (paginated)
- `GET /purchase-requests` → list all requests (paginated + filters)
- `GET /purchase-requests/{uuid}` → show a request
- `PATCH /purchase-requests/{uuid}/approve` → approve
- `PATCH /purchase-requests/{uuid}/decline` → decline
- `PATCH /purchase-requests/{uuid}/process` → process (cash/bank)
- `GET /deleted-purchase-requests` → deleted list (paginated + filters)
- `POST /refresh-queue` → refresh FX queue

### Miscs
- `GET /miscs/decline-reasons`
- `GET /miscs/purchase-request-states`
- `GET /miscs/purchase-request-types`
- `GET /miscs/exchange-rates?filter[date]=YYYY-MM-DD`

### Exchange Rates
- `GET /exchange-rates` → paginated exchange rates

### Reports
- `GET /reports/purchase-requests-states?filter[approved_on]=YYYY-MM-DD&filter[type]=cash|bank_transfer`

---

## 5) cURL + Response Examples (from Postman)

> Replace `BASE_URL` and `FCMS_FX_TOKEN` with your values.

### Create Contract
```bash
curl -X POST "${BASE_URL}/api/v1/contracts" \
  -H "Authorization: Bearer ${FCMS_FX_TOKEN}" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "cash_price": "5.74",
    "bank_transfer_price": "5.84",
    "amount": "10000000"
  }'
```

**Response**
```json
{
  "data": {
    "uuid": "660e8400-e29b-41d4-a716-446655440001",
    "amount": 10000,
    "cash_price": 5.8,
    "bank_transfer_price": 5.84,
    "state": { "code": "pending", "name": "قيد الانتظار" },
    "created_at": "2026-01-06T10:45:37.000000Z"
  }
}
```

### List Pending Requests (Queue)
```bash
curl -X GET "${BASE_URL}/api/v1/purchase-requests-queue?page=1" \
  -H "Authorization: Bearer ${FCMS_FX_TOKEN}" \
  -H "Accept: application/json"
```

**Response**
```json
{
  "data": [
    {
      "uuid": "550e8400-e29b-41d4-a716-446655440000",
      "reference": "FCPR-002-20222224-GHWBXWXKQQZH",
      "amount_requested": 5000.0,
      "type": { "code": "personal", "name": "شخصي" },
      "state": { "code": "pending", "name": "قيد الانتظار" },
      "deposit_type": { "code": "cash", "name": "نقدي" },
      "passport_attached": true
    }
  ]
}
```

### Approve Purchase Request
```bash
curl -X PATCH "${BASE_URL}/api/v1/purchase-requests/${UUID}/approve" \
  -H "Authorization: Bearer ${FCMS_FX_TOKEN}" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{ "ts": 1705343023 }'
```

**Response**
```json
{
  "data": {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "state": { "code": "approved", "name": "موافق عليه" },
    "approved_at": "2026-01-05T09:15:00.000000Z",
    "deposit_type": { "code": "cash", "name": "نقدي" },
    "passport_attached": true
  }
}
```

### Decline Purchase Request
```bash
curl -X PATCH "${BASE_URL}/api/v1/purchase-requests/${UUID}/decline" \
  -H "Authorization: Bearer ${FCMS_FX_TOKEN}" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "ts": 1705343023,
    "decline_reason": "inactive_or_dormant_account",
    "decline_reason_note": ""
  }'
```

**Response**
```json
{
  "data": {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "state": { "code": "declined", "name": "مرفوض" },
    "decline_reason": { "code": "invalid_documents", "name": "مستندات غير صالحة" },
    "decline_reason_note": "جواز السفر منتهي الصلاحية"
  }
}
```

### Process Purchase Request
```bash
curl -X PATCH "${BASE_URL}/api/v1/purchase-requests/${UUID}/process" \
  -H "Authorization: Bearer ${FCMS_FX_TOKEN}" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{ "ts": 1705341004, "type": "card" }'
```

**Response**
```json
{
  "data": {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "state": { "code": "processed", "name": "تمت المعالجة" },
    "processed_type": { "code": "cash", "name": "نقدي" },
    "processed_at": "2026-01-05T10:00:00.000000Z"
  }
}
```

### Miscs (Decline Reasons)
```bash
curl -X GET "${BASE_URL}/api/v1/miscs/decline-reasons" \
  -H "Authorization: Bearer ${FCMS_FX_TOKEN}" \
  -H "Accept: application/json"
```

**Response**
```json
{
  "data": [
    { "code": "could_not_contact_customer", "name": "لم نتمكن من الاتصال بالعميل" },
    { "code": "CUSTOMER_DID_NOT_TRANSFER_OR_DEPOSIT_AMOUNT_IN_LIBYAN_DINARS", "name": "لم يقم الزبون بتحويل أو ايداع المبلغ بالدينار الليبي" }
  ]
}
```

### Exchange Rates
```bash
curl -X GET "${BASE_URL}/api/v1/exchange-rates?filter[date]=2026-02-02" \
  -H "Authorization: Bearer ${FCMS_FX_TOKEN}" \
  -H "Accept: application/json"
```

**Response**
```json
{
  "data": [
    { "date": "2026-01-01", "rate": 5.44 },
    { "date": "2026-01-04", "rate": 5.44 }
  ],
  "meta": { "current_page": 1, "per_page": 15, "total": 3 }
}
```

### Reports (Purchase Requests States)
```bash
curl -X GET "${BASE_URL}/api/v1/reports/purchase-requests-states?filter[approved_on]=2026-01-05&filter[type]=cash" \
  -H "Authorization: Bearer ${FCMS_FX_TOKEN}" \
  -H "Accept: application/json"
```

**Response**
```json
{
  "data": {
    "approved": { "count": 45, "total": 225000.0 },
    "processed": { "count": 38, "total": 190000.0 },
    "declined": { "count": 7, "total": 35000.0 }
  }
}
```

---

## 6) Example: Configure + Use the FX Houses Client

```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.requests.FcmsFxRequestsClients
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestActionRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestProcessRequest

suspend fun main() {
    val config = FcmsFxConfig(
        baseUrl = System.getenv("FCMS_BASE_URL"),
        tokenProvider = { System.getenv("FCMS_FX_TOKEN") }
    )

    val requests = FcmsFxRequestsClients.create(config)

    val queue = requests.listPendingRequests(page = 1)
    val first = queue.data.firstOrNull() ?: return

    requests.approvePurchaseRequest(
        first.uuid!!,
        PurchaseRequestActionRequest(ts = System.currentTimeMillis() / 1000)
    )

    requests.processPurchaseRequest(
        first.uuid!!,
        PurchaseRequestProcessRequest(
            ts = System.currentTimeMillis() / 1000,
            type = "cash"
        )
    )

    requests.close()
}
```

---

## 7) More Examples (by Module)

### Contracts
```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.contracts.FcmsFxContractsClients
import ly.neptune.nexus.fcms.fxhousesclient.contracts.model.FxContractCreateRequest

val contracts = FcmsFxContractsClients.create(config)
val created = contracts.create(
    FxContractCreateRequest(
        cashPrice = "5.120",
        bankTransferPrice = "5.150",
        amount = "10000"
    )
)
val list = contracts.list(page = 1)
contracts.close()
```

### Purchase Requests (list + approve/decline/process)
```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.requests.FcmsFxRequestsClients
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestActionRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestDeclineRequest
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestProcessRequest

val requests = FcmsFxRequestsClients.create(config)
val queue = requests.listPendingRequests(page = 1)
val first = queue.data.firstOrNull() ?: return

requests.approvePurchaseRequest(first.uuid!!, PurchaseRequestActionRequest(ts = System.currentTimeMillis() / 1000))
requests.declinePurchaseRequest(first.uuid!!, PurchaseRequestDeclineRequest(ts = System.currentTimeMillis() / 1000, declineReason = "documents", declineReasonNote = "Missing passport"))
requests.processPurchaseRequest(first.uuid!!, PurchaseRequestProcessRequest(ts = System.currentTimeMillis() / 1000, type = "cash"))
requests.close()
```

### Miscs (decline reasons + request states)
```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.miscs.FcmsFxMiscsClients

val miscs = FcmsFxMiscsClients.create(config)
val reasons = miscs.listDeclineReasons()
val states = miscs.listPurchaseRequestStates()
val types = miscs.listPurchaseRequestTypes()
val ratesByDate = miscs.listExchangeRates(dateFilter = "2025-02-01")
miscs.close()
```

### Exchange Rates (paginated)
```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.exchangerates.FcmsFxExchangeRatesClients

val fxRates = FcmsFxExchangeRatesClients.create(config)
val page = fxRates.list(page = 1)
fxRates.close()
```

### Reports
```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.reports.FcmsFxReportsClients

val reports = FcmsFxReportsClients.create(config)
val summary = reports.purchaseRequestsStatesSummary(approvedOn = "2025-02-01", type = "cash")
reports.close()
```

---

## 8) Updating to the New Version (Bank Client)

If you are already using the **bank-side** SDK (`fcms-client`), update to the latest release (currently `1.2.0`).

### Gradle
```kotlin
dependencies {
    implementation("io.github.tellesy:fcms-client:1.2.0")
}
```

### Maven
```xml
<dependency>
  <groupId>io.github.tellesy</groupId>
  <artifactId>fcms-client</artifactId>
  <version>1.2.0</version>
</dependency>
```

---

## 9) Notes & Best Practices

- Keep **FX house token** separate from **bank token**.
- Always call `close()` on clients to shut down OkHttp and avoid JVM hang.
- If using Spring Boot, wire clients as beans and close them on shutdown.

---

## 10) Need Help?
If you want me to add more examples or extend this doc, just say the word.
