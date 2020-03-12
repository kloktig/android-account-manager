package no.kloktig.library

import AuthLocalStorage
import AuthPreferences
import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import no.kloktig.library.RegisterActivity.Companion.ACCOUNT_TYPE
import no.kloktig.library.RegisterActivity.Companion.KEY_USERID


class TokenHandler {
    private lateinit var am: AccountManager
    private lateinit var prefs: AuthPreferences
    private var authToken: String? = null

    fun getToken(activity: Activity, requestCode: Int, uiCallback: (AuthLocalStorage) -> Unit) {
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
        val uiCallback: (AuthLocalStorage) -> Unit
    ) : AccountManagerCallback<Bundle> {
        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle = result.result
            val intent = bundle[AccountManager.KEY_INTENT] as Intent?
            val am = AccountManager.get(activity)

            if (null != intent) {
                ActivityCompat.startActivityForResult(activity, intent, requestCode, null)
            } else {
                bundle.getString(AccountManager.KEY_AUTHTOKEN)?.let { prefs.refreshToken = it }
                bundle.getString(AccountManager.KEY_ACCOUNT_NAME)?.let { prefs.username = it }
                am.getAccountsByType(ACCOUNT_TYPE).firstOrNull()?.let { account ->
                    am.getUserData(account, KEY_USERID)?.let {
                        prefs.userId = it
                    }
                }
                uiCallback.invoke(prefs)
            }
        }
    }
}