package no.kloktig.library

import AuthLocalStorage
import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.os.Bundle
import no.kloktig.library.infrastructure.AppModule
import no.kloktig.library.storage.AuthSendDown
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.lang.Exception

class RegisterActivity : AccountAuthenticatorActivity() {
    private val storage: AuthLocalStorage by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val am = AccountManager.get(this)
        registerAccount(am)
    }

    private fun registerAccount(am: AccountManager) {
        val accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)
        val params = AuthSendDown(context = this)

        Account(params.username, accountType).let { account ->
            am.addAccountExplicitly(account, null, null)
            am.setAuthToken(account, AUTH_TOKEN_TYPE,  "${params.userId}:${params.refreshToken}")
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, params.username)
            storage.apply {
                userId = params.userId
                refreshToken = params.refreshToken
            }
        }

        setAccountAuthenticatorResult(intent.extras)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        val ACCOUNT_TYPE = "no.kloktig.example"
        val AUTH_TOKEN_TYPE = "no.klokig.example.refresh"
    }
}

