package no.kloktig.accountmanager

import AuthPreferences
import android.accounts.*
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import no.kloktig.accountmanager.RegisterActivity.Companion.ACCOUNT_TYPE
import no.kloktig.accountmanager.RegisterActivity.Companion.AUTH_TOKEN_TYPE

class MainActivity : Activity() {
    private lateinit var am: AccountManager
    private lateinit var prefs: AuthPreferences

    private var authToken: String? = null

    companion object {
        private val REGISTER = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        val userInfo = android.R.id.text1.getView<TextView>()
        val tokenInfo = android.R.id.text2.getView<TextView>()

        authToken = null
        prefs = AuthPreferences(this)
        am = AccountManager.get(this)

        am.getAuthTokenByFeatures(
            ACCOUNT_TYPE,
            AUTH_TOKEN_TYPE,
            null,
            this,
            null,
            null,
            GetAuthTokenCallback(prefs = prefs, am = am, tokenInfo = tokenInfo, userInfo = userInfo),
            null
        )
    }

    private class GetAuthTokenCallback(val prefs: AuthPreferences, val am: AccountManager, val tokenInfo: TextView, val userInfo: TextView ) : AccountManagerCallback<Bundle> {
        val tokenMessage
            get()  ="Token: ${prefs.authToken}"

        val userMessage
            get()  ="User: ${prefs.username} "

        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle = result.result
            val intent = bundle[AccountManager.KEY_INTENT] as Intent?

            if (null != intent) {
                startActivityForResult(MainActivity(), intent, REGISTER, null)
            } else {
                bundle.getString(AccountManager.KEY_AUTHTOKEN)?.let { prefs.authToken = it }
                bundle.getString(AccountManager.KEY_ACCOUNT_NAME)?.let { prefs.username = it }

                tokenInfo.text = tokenMessage
                userInfo.text = userMessage

                val account = am.getAccountsByType(ACCOUNT_TYPE).first() // TODO: Better handling

                if (null == account) {
                    Account(prefs.username, ACCOUNT_TYPE).apply {
                        am.addAccountExplicitly(this, null, null)
                        am.setAuthToken(account, AUTH_TOKEN_TYPE, prefs.authToken)
                    }
                }
            }
        }
    }

    private fun <T> Int.getView() = findViewById<View>(this) as T
}

