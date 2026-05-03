# FCMS Client SDK - New Response Fields Documentation

**Version:** 1.4.6 / 1.1.11  
**Date:** May 3, 2026

## Overview

This document describes the new fields added to the SDK models to match the latest API responses from FCMS. These changes ensure that your Java mapper can access all the data returned by the API without needing to parse the raw JSON tree.

## New Fields in FxPurchaseRequest

The `FxPurchaseRequest` model now includes the following additional fields:

### Approval and Decline Fields
- **`approvedAt`** (String): Timestamp when the request was approved
- **`approvedBy`** (String): ID of the user who approved the request
- **`declinedAt`** (String): Timestamp when the request was declined
- **`declinedBy`** (String): ID of the user who declined the request

### Cost and Exchange Rate
- **`cost`** (String): The calculated cost of the purchase request (e.g., "12824.58")
- **`exchangeRate`** (ExchangeRate): Object containing exchange rate information
  - `date`: The date of the exchange rate
  - `rate`: The exchange rate value (e.g., "6.33")

### Processing Fields
- **`processedType`** (CodeName): The type of processing applied
- **`processedAt`** (String): Timestamp when the request was processed
- **`processedBy`** (String): ID of the user who processed the request
- **`updatedFrom`** (String): Source of the update

### Bank Processing Fields
- **`bankProcessedAt`** (String): Timestamp when the bank processed the request
- **`bankApprovedAt`** (String): Timestamp when the bank approved the request
- **`bankDeclinedAt`** (String): Timestamp when the bank declined the request

### Deletion Fields
- **`isDeleted`** (Boolean): Whether the request has been deleted
- **`deletedAt`** (String): Timestamp when the request was deleted

### Branch Information
- **`bankBranch`** (BankBranch): Bank branch information
  - `uuid`: Branch UUID
  - `name`: Branch name
  - `isActive`: Whether the branch is active ("0" or "1")
  - `isMainBranch`: Whether this is the main branch ("0" or "1")
  - `createdAt`: Branch creation timestamp
- **`usdProviderBranch`** (UsdProviderBranch): USD provider branch information
  - `uuid`: Branch UUID
  - `name`: Branch name
  - `isActive`: Whether the branch is active ("0" or "1")
  - `isMainBranch`: Whether this is the main branch ("0" or "1")
  - `createdAt`: Branch creation timestamp

## New Fields in CashContract

The `CashContract` model now includes the following additional fields:

### Fee and Amount Fields
- **`fxFee`** (String): The FX fee rate (e.g., "0.3000")
- **`sumOfApprovedCashAmount`** (String): Total approved cash amount
- **`sumOfApprovedBankTransferAmount`** (String): Total approved bank transfer amount

**Note:** The model retains the original `cash_price`, `bank_transfer_price`, `sum_of_approved_cash_price`, and `sum_of_approved_bank_transfer_price` fields for backward compatibility, but the API now sends `fx_fee`, `sum_of_approved_cash_amount`, and `sum_of_approved_bank_transfer_amount`.

## New Fields in FxContract

The `FxContract` model now includes the same additional fields as `CashContract`:

### Fee and Amount Fields
- **`fxFee`** (String): The FX fee rate (e.g., "0.3000")
- **`sumOfApprovedCashAmount`** (String): Total approved cash amount
- **`sumOfApprovedBankTransferAmount`** (String): Total approved bank transfer amount

## New Model Classes

### ExchangeRate
```kotlin
data class ExchangeRate(
    val date: String?,
    val rate: String?
)
```

### BankBranch
```kotlin
data class BankBranch(
    val uuid: String?,
    val name: String?
)
```

### UsdProviderBranch
```kotlin
data class UsdProviderBranch(
    val uuid: String?,
    val name: String?,
    val isActive: String?,
    val isMainBranch: String?,
    val createdAt: String?
)
```

## Usage Examples

### Accessing Cost and Exchange Rate
```java
FxPurchaseRequest request = ...;
String cost = request.getCost(); // "12824.58"
String rate = request.getExchangeRate().getRate(); // "6.33"
String rateDate = request.getExchangeRate().getDate(); // "2026-04-30"
```

### Accessing Contract Fee Information
```java
FxContract contract = request.getContract();
String fxFee = contract.getFxFee(); // "0.3000"
String cashAmount = contract.getSumOfApprovedCashAmount(); // "0"
String bankAmount = contract.getSumOfApprovedBankTransferAmount(); // "0"
```

### Accessing Cash Contract Fee Information
```java
CashContract cashContract = ...;
String fxFee = cashContract.getFxFee(); // "0.4400"
String cashAmount = cashContract.getSumOfApprovedCashAmount(); // "0"
String bankAmount = cashContract.getSumOfApprovedBankTransferAmount(); // "0"
```

## Important Notes for Mappers

1. **Cost Field**: The `cost` field is now directly accessible on `FxPurchaseRequest` via `getCost()`. You no longer need to calculate it from other fields.

2. **Exchange Rate**: The exchange rate is now available as a structured object via `getExchangeRate()`. Access the rate value with `getExchangeRate().getRate()`.

3. **Fee Fields**: The API now sends `fx_fee` instead of `cash_price` in some contexts. The SDK maps both field names to ensure compatibility.

4. **Amount Fields**: The API now uses `sum_of_approved_cash_amount` and `sum_of_approved_bank_transfer_amount` instead of the `*_price` variants. The SDK supports both field names.

5. **Backward Compatibility**: All original fields are retained in the models to ensure existing code continues to work.

## Migration Guide

If your mapper was previously accessing these fields from the raw JSON tree, you can now update it to use the direct getters:

**Before:**
```java
JsonNode raw = request.getRaw();
String cost = raw.get("cost").asText();
String rate = raw.get("exchange_rate").get("rate").asText();
```

**After:**
```java
String cost = request.getCost();
String rate = request.getExchangeRate().getRate();
```

This change makes your code more type-safe and less dependent on the JSON structure.
