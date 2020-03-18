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
    companion object{
        var num = 0
    }

    fun getToken(activity: Activity, requestCode: Int, uiCallback: (AuthLocalStorage) -> Unit) {
        // Write to a "Downward buffer" (using shared preferences). These are read by to registration Activity
        AuthSendDown.setValues(activity, "MyUserName", "MyUserId", "MyRefreshToken")
        create(activity, requestCode, uiCallback)
    }

    fun updateToken(activity: Activity, requestCode: Int, uiCallback: (AuthLocalStorage) -> Unit) {
        // Write to a "Downward buffer" (using shared preferences). These are read by to registration Activity
        AuthSendDown.setValues(activity, "MyUserName", "MyUserId", "MyUpdatedToken${num++}")
        update(activity, requestCode, uiCallback)
    }


    private fun create(activity: Activity, requestCode: Int, uiCallback: (AuthLocalStorage) -> Unit) {
        val am = AccountManager.get(activity)
        am.getAuthTokenByFeatures(
            RegisterActivity.ACCOUNT_TYPE,
            RegisterActivity.AUTH_TOKEN_TYPE,
            null,
            activity,
            null,
            null,
            GetAuthTokenCallback(
                activity = activity,
                requestCode = requestCode,
                uiCallback = uiCallback
            ),
            null
        )
    }

    private fun update(activity: Activity, requestCode: Int, uiCallback: (AuthLocalStorage) -> Unit) {
        val am = AccountManager.get(activity)
        am.getAccountsByType(RegisterActivity.ACCOUNT_TYPE).let {
            am.updateCredentials(
                it.first(),
                RegisterActivity.AUTH_TOKEN_TYPE,
                null,
                activity,
                UpdateAuthTokenCallback(
                    activity = activity,
                    requestCode = requestCode,
                    uiCallback = uiCallback
                ),
                null
            )
        }
    }

    private class GetAuthTokenCallback(
        private val activity: Activity,
        private val requestCode: Int,
        val uiCallback: (AuthLocalStorage) -> Unit
    ) : AccountManagerCallback<Bundle> {
        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle = result.result
            val intent = bundle[AccountManager.KEY_INTENT] as Intent?

            if (null != intent) {
                ActivityCompat.startActivityForResult(activity, intent, requestCode, null)
            } else {
                uiCallback.invoke(AuthPreferences.from(activity, bundle))
            }
        }
    }

    private class UpdateAuthTokenCallback(
        private val activity: Activity,
        private val requestCode: Int,
        val uiCallback: (AuthLocalStorage) -> Unit
    ) : AccountManagerCallback<Bundle> {
        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle = result.result
            val intent = bundle[AccountManager.KEY_INTENT] as Intent?

            if (null != intent) {
                ActivityCompat.startActivityForResult(activity, intent, requestCode, null)
            } else {
                uiCallback.invoke(AuthPreferences.from(activity, bundle))
            }

        }
    }
}