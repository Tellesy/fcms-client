# FCMS Client SDK v1.4.2 Release Notes

**Release Date:** May 3, 2026  
**Version:** 1.4.2 (fcms-client), 1.1.7 (fcms-fx-houses-client)  
**Maven Central:** `io.github.tellesy:fcms-client:1.4.2`

## Updates in 1.4.2 & 1.1.7
- Corrected path structures: All cash-related APIs are now correctly routed to `/api/v1/fx-houses/cash/`.
  - `listPurchaseRequestsQueue` hits `/api/v1/fx-houses/cash/purchase-requests-queue`
  - `approveFxPurchaseRequest` hits `/api/v1/fx-houses/cash/purchase-requests/{uuid}/approve`
  - `listCashContracts` hits `/api/v1/fx-houses/cash/cash-contracts`
  - *(And similarly for all other cash functions)*

---

## What's New in 1.4.0

This release adds comprehensive Cash API support and new Purchase Request management endpoints to the FX Houses module.

### New Features

#### 1. Cash Contracts API (New Feature)

A complete new set of endpoints for managing cash contracts:

- **`listCashContracts(page, filter)`** - List all cash contracts with filtering options
- **`approveCashContract(uuid, request)`** - Approve a pending cash contract
- **`processCashContract(uuid, request)`** - Process an approved cash contract
- **`rejectCashContract(uuid, request)`** - Reject a cash contract

**New Models:**
- `CashContract` - Response model for cash contract data
- `CashContractsListFilter` - Filter by date range, state, and CBL key
- `CashContractActionRequest` - Request model for approve action
- `CashContractProcessRequest` - Request model for process action (supports `usd_serial_numbers`)
- `CashContractRejectRequest` - Request model for reject action with optional reason

#### 2. Purchase Request Actions

The SDK can now perform actions on FX purchase requests:

- **`approveFxPurchaseRequest(uuid, request)`** - Approve a purchase request
- **`processFxPurchaseRequest(uuid, request)`** - Process an approved purchase request (supports `usd_serial_numbers`)
- **`declineFxPurchaseRequest(uuid, request)`** - Decline a purchase request

**New Models:**
- `FxPurchaseRequestApproveRequest` - Simple timestamp request for approval
- `FxPurchaseRequestProcessRequest` - Process request with optional `usd_serial_numbers: List<String>?`
- `FxPurchaseRequestDeclineRequest` - Decline request with optional reason fields

#### 3. Purchase Request List Variants

New specialized listing endpoints for purchase requests:

- **`listPendingPurchaseRequests(page, filter)`** - List pending purchase requests only
  - Filter options: `reference`, `phone`, `nid`
- **`listPurchaseRequestsQueue(page, filter)`** - List purchase requests queue
  - Filter options: `type` (values: `cash`, `bank_transfer`, `card`)

**Purchase Request Types:**
The `type` field on `FxPurchaseRequest` is a `CodeName` with these possible values:
- `cash` - Cash purchase request
- `bank_transfer` - Bank transfer purchase request  
- `card` - Card purchase request

**New Models:**
- `PendingPurchaseRequestsListFilter`
- `PurchaseRequestsQueueListFilter`

### Java Support

All new methods are available via `FcmsFxHousesClientJava` with `CompletableFuture` wrappers:

```java
// Example: Process a purchase request with USD serial numbers
var request = new FxPurchaseRequestProcessRequest(
    System.currentTimeMillis(),
    List.of("ABC123456", "DEF789012")
);
client.processFxPurchaseRequest(uuid, request, null)
    .thenAccept(result -> System.out.println("Processed: " + result.getUuid()));
```

### Kotlin Usage

```kotlin
// Example: Approve a cash contract
val request = CashContractActionRequest(ts = System.currentTimeMillis())
val result = client.approveCashContract(uuid, request)

// Example: List cash contracts with filter
val filter = CashContractsListFilter(
    dateFrom = "2026-04-01",
    dateTo = "2026-04-30",
    state = "pending"
)
val contracts = client.listCashContracts(page = 1, filter = filter)
```

## Files Added/Modified

- `src/main/kotlin/ly/neptune/nexus/fcms/fxhouses/model/Models.kt` - Added `CashContract`, `CashContractsListFilter`, `PendingPurchaseRequestsListFilter`, `PurchaseRequestsQueueListFilter`
- `src/main/kotlin/ly/neptune/nexus/fcms/fxhouses/model/request/Requests.kt` - Added all new request models
- `src/main/kotlin/ly/neptune/nexus/fcms/fxhouses/FcmsFxHousesClient.kt` - Added interface methods
- `src/main/kotlin/ly/neptune/nexus/fcms/fxhouses/internal/FcmsFxHousesClientImpl.kt` - Added implementations
- `src/main/kotlin/ly/neptune/nexus/fcms/fxhouses/FcmsFxHousesClientJava.kt` - Added Java wrappers

## Migration Guide

No breaking changes. This is a backward-compatible feature addition.

## Bug Fixes

None in this release (bug fixes were released in v1.3.6/v1.3.7).

## Dependencies

No changes to dependencies in this release.
