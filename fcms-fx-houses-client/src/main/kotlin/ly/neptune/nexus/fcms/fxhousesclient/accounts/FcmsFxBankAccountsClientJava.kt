package ly.neptune.nexus.fcms.fxhousesclient.accounts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import ly.neptune.nexus.fcms.fxhousesclient.accounts.internal.FcmsFxBankAccountsClientImpl
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.core.RequestOptions
import ly.neptune.nexus.fcms.fxhousesclient.core.model.Page
import ly.neptune.nexus.fcms.fxhousesclient.requests.model.BankAccount
import java.util.concurrent.CompletableFuture

class FcmsFxBankAccountsClientJava private constructor(
    private val delegate: FcmsFxBankAccountsClient,
) : AutoCloseable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun listBankAccounts(page: Int?, options: RequestOptions?): CompletableFuture<Page<BankAccount>> =
        scope.future { delegate.listBankAccounts(page, options) }

    override fun close() {
        scope.cancel("FcmsFxBankAccountsClientJava closed")
        delegate.close()
    }

    companion object {
        @JvmStatic
        fun create(config: FcmsFxConfig): FcmsFxBankAccountsClientJava {
            val impl: FcmsFxBankAccountsClient = FcmsFxBankAccountsClientImpl(config)
            return FcmsFxBankAccountsClientJava(impl)
        }
    }
}
