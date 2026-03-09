package ly.neptune.nexus.fcms.fxhousesclient.accounts

import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.core.model.Page
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.BankAccount

interface FcmsFxBankAccountsClient : AutoCloseable {
    suspend fun listBankAccounts(
        page: Int? = null,
        options: RequestOptions? = null,
    ): Page<BankAccount>
}
