package no.kloktig.accountmanager

import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuthenticatorService : Service() {
    private val authenticator: Lazy<AccountAuthenticator>
        get() = lazy { AccountAuthenticator(context = this) }

    override fun onBind(intent: Intent): IBinder? =
        when (intent.action) {
            AccountManager.ACTION_AUTHENTICATOR_INTENT -> authenticator.value.iBinder
            else -> null
        }
}