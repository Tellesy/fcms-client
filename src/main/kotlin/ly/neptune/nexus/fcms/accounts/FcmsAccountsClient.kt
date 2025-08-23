package ly.neptune.nexus.fcms.accounts

import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.salaries.model.Page
import ly.neptune.nexus.fcms.accounts.model.BankAccount
import ly.neptune.nexus.fcms.accounts.model.request.MatchBankAccountRequest
import ly.neptune.nexus.fcms.accounts.model.request.UpdateBankAccountRequest

/**
 * FCMS Accounts client interface for managing bank accounts.
 */
interface FcmsAccountsClient : AutoCloseable {
    /** List bank accounts with optional pagination and filters. */
    suspend fun listAccounts(
        page: Int? = null,
        filter: AccountsListFilter? = null,
        options: RequestOptions? = null
    ): Page<BankAccount>

    /** Match a bank account using customer identifiers. */
    suspend fun matchAccount(
        uuid: String,
        request: MatchBankAccountRequest,
        options: RequestOptions? = null
    ): BankAccount

    /** Reject a bank account with a reason and optional internal note. */
    suspend fun rejectAccount(
        uuid: String,
        rejectReason: String,
        rejectReasonNote: String? = null,
        options: RequestOptions? = null
    ): BankAccount

    /** Return a rejected account back to pending state. */
    suspend fun unrejectAccount(
        uuid: String,
        options: RequestOptions? = null
    ): BankAccount

    /** Update a bank account meta such as account number or currency. */
    suspend fun updateAccount(
        uuid: String,
        request: UpdateBankAccountRequest,
        options: RequestOptions? = null
    ): BankAccount
}

/** Query filters for listing bank accounts. */
data class AccountsListFilter(
    val state: String? = null,
    val iban: String? = null,
    val createdOn: String? = null,     // yyyy-MM-dd
    val approvedOn: String? = null,    // yyyy-MM-dd
    val rejectedOn: String? = null,    // yyyy-MM-dd
    val unrejectedOn: String? = null,  // yyyy-MM-dd
    val accountNumber: String? = null,
    val hasAccountNumber: Boolean? = null,
)
