package no.kloktig.library

import AuthPreferences
import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat

class TokenHandler {
    private lateinit var am: AccountManager
    private lateinit var prefs: AuthPreferences
    private var authToken: String? = null

    fun getToken(activity: Activity, requestCode: Int, uiCallback: (user: String, token: String) -> Unit) {
        authToken = null
        prefs = AuthPreferences(activity)
        am = AccountManager.get(activity)

        am.getAuthTokenByFeatures(
            RegisterActivity.ACCOUNT_TYPE,
            RegisterActivity.AUTH_TOKEN_TYPE,
            null,
            activity,
            null,
            null,
            GetAuthTokenCallback(
                activity = activity,
                prefs = prefs,
                am = am,
                requestCode = requestCode,
                uiCallback = uiCallback
            ),
            null
        )
    }

    private class GetAuthTokenCallback(
        private val activity: Activity,
        private val requestCode: Int,
        private val prefs: AuthPreferences,
        private val am: AccountManager,
        val uiCallback: (user: String, token: String) -> Unit
    ) : AccountManagerCallback<Bundle> {
        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle = result.result
            val intent = bundle[AccountManager.KEY_INTENT] as Intent?

            if (null != intent) {
                ActivityCompat.startActivityForResult(activity, intent, requestCode, null)
            } else {
                bundle.getString(AccountManager.KEY_AUTHTOKEN)?.let { prefs.authToken = it }
                bundle.getString(AccountManager.KEY_ACCOUNT_NAME)?.let { prefs.username = it }

                uiCallback.invoke(prefs.username, prefs.authToken)

                val account = am.getAccountsByType(RegisterActivity.ACCOUNT_TYPE).first() // TODO: Better handling

                if (null == account) {
                    Account(prefs.username, RegisterActivity.ACCOUNT_TYPE).apply {
                        am.addAccountExplicitly(this, null, null)
                        am.setAuthToken(account, RegisterActivity.AUTH_TOKEN_TYPE, prefs.authToken)
                    }
                }
            }
        }
    }

}