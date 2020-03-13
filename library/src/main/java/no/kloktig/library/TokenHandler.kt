package no.kloktig.library

import AuthLocalStorage
import AuthPreferences
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import no.kloktig.library.storage.AuthSendDown

class TokenHandler {
    private lateinit var am: AccountManager
    private lateinit var prefs: AuthPreferences
    private var authToken: String? = null

    fun getToken(activity: Activity, requestCode: Int, uiCallback: (AuthLocalStorage) -> Unit) {
        authToken = null
        prefs = AuthPreferences(activity)
        am = AccountManager.get(activity)

        // Write to a "Downward buffer" (using shared preferences). These are read by to registration Activity
        AuthSendDown.setValues(activity, "MyUserName", "MyUserId", "MyRefreshToken" )

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
        val uiCallback: (AuthLocalStorage) -> Unit
    ) : AccountManagerCallback<Bundle> {
        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle = result.result
            val intent = bundle[AccountManager.KEY_INTENT] as Intent?

            if (null != intent) {
                ActivityCompat.startActivityForResult(activity, intent, requestCode, null)
            } else {
                bundle.getString(AccountManager.KEY_ACCOUNT_NAME)?.let { prefs.username = it }
                bundle.getString(AccountManager.KEY_AUTHTOKEN)?.let {
                    val parts = it.split(":")
                    prefs.userId = parts[0]
                    prefs.refreshToken = parts[1]
                }
                uiCallback.invoke(prefs)
            }
        }
    }
}