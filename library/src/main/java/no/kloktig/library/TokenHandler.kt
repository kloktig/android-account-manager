package no.kloktig.library

import AuthLocalStorage
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import no.kloktig.library.storage.AuthSendDown
import org.koin.java.KoinJavaComponent.inject
import java.lang.Error
import java.lang.Exception

class TokenHandler {

    companion object {
        var num = 0
    }

    fun getToken(activity: Activity, requestCode: Int) {
        // Write to a "Downward buffer" (using shared preferences). These are read by to registration Activity
        AuthSendDown.setValues(activity, "MyUserName", "MyUserId", "MyRefreshToken")
        create(activity, requestCode)
    }

    fun updateToken(activity: Activity, requestCode: Int) {
        // Write to a "Downward buffer" (using shared preferences). These are read by to registration Activity
        AuthSendDown.setValues(activity, "MyUserName", "MyUserId", "${activity.componentName.packageName}-Token-${num++}")
        update(activity, requestCode)
    }

    private fun create(activity: Activity, requestCode: Int) {
        val am = AccountManager.get(activity)
        am.getAuthTokenByFeatures(
            RegisterActivity.ACCOUNT_TYPE,
            RegisterActivity.AUTH_TOKEN_TYPE,
            null,
            activity,
            null,
            null,
            TokenCallback(
                activity = activity,
                requestCode = requestCode
            ),
            null
        )
    }

    private fun update(activity: Activity, requestCode: Int) {
        val am = AccountManager.get(activity)
        am.getAccountsByType(RegisterActivity.ACCOUNT_TYPE).let {
            if(it.isEmpty()) {
               create(activity, requestCode)
            } else {
                try {
                    am.updateCredentials(
                        it.first(),
                        RegisterActivity.AUTH_TOKEN_TYPE,
                        null,
                        activity,
                        TokenCallback(
                            activity = activity,
                            requestCode = requestCode
                        ),
                        null
                    )
                } catch (ex: Exception) {
                    val e = ex
                }
            }
        }
    }

    private class TokenCallback(
        private val activity: Activity,
        private val requestCode: Int
    ) : AccountManagerCallback<Bundle> {
        val storage by inject(AuthLocalStorage::class.java)

        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle = result.result
            val intent = bundle[AccountManager.KEY_INTENT] as Intent?

            if (null != intent) {
                ActivityCompat.startActivityForResult(activity, intent, requestCode, null)
            } else {
                storage.apply {
                    bundle.getString(AccountManager.KEY_AUTHTOKEN)?.let { usernameAndToken ->
                        val parts = usernameAndToken.split(":")
                        userId = parts[0]
                        refreshToken = parts[1]
                    }

                }
            }

        }
    }
}