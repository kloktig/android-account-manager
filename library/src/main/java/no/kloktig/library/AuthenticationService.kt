package no.kloktig.library

import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import no.kloktig.library.AccountAuthenticator

class AuthenticatorService : Service() {
    private val authenticator: Lazy<no.kloktig.library.AccountAuthenticator>
        get() = lazy { no.kloktig.library.AccountAuthenticator(context = this) }

    override fun onBind(intent: Intent): IBinder? =
        when (intent.action) {
            AccountManager.ACTION_AUTHENTICATOR_INTENT -> authenticator.value.iBinder
            else -> null
        }
}