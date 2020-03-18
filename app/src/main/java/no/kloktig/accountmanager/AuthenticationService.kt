package no.kloktig.accountmanager

import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import no.kloktig.library.AccountAuthenticator
import no.kloktig.library.infrastructure.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AuthenticatorService : Service() {

    private val authenticator: Lazy<AccountAuthenticator>
        get() = lazy { AccountAuthenticator(context = this) }

    override fun onBind(intent: Intent): IBinder? =
        when (intent.action) {
            AccountManager.ACTION_AUTHENTICATOR_INTENT -> authenticator.value.iBinder
            else -> null
        }
}