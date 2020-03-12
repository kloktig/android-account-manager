package no.kloktig.library

import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import kotlin.system.exitProcess

class RegisterActivity : AccountAuthenticatorActivity(), View.OnClickListener {
    private lateinit var am: AccountManager
    private lateinit var user: String
    private lateinit var password: String

    private lateinit var viewParams: ViewParameters

    internal class ViewParameters(
        val email: EditText,
        val password: EditText,
        val loginButton: Button
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_login)

        am = AccountManager.get(this)
        user = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) ?: ""
        viewParams = setupView()
    }

    override fun onClick(view: View) {
        if (view == viewParams.loginButton)
            attemptLogin()
    }

    private fun attemptLogin() {
        user = viewParams.email.text.toString()
        password = viewParams.password.text.toString()

        val authToken = viewParams.password.text.toString()
        val accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)

        Account(user, accountType).let { account ->
            am.addAccountExplicitly(account, null, null)
            am.setUserData(account, KEY_USERID, "MyUniqueID")
            am.setAuthToken(account, AUTH_TOKEN_TYPE, authToken)
            setAccountAuthenticatorResult(intent.extras)
            setResult(RESULT_OK, intent)
        }

        exitProcess(0)
    }

    private fun <T> Int.get() = findViewById<View>(this) as T

    private fun setupView(): ViewParameters {
        return ViewParameters(
            email = R.id.email.get<EditText>().apply { setText(user) },
            password = R.id.password.get(),
            loginButton = R.id.login_btn.get<Button>().apply { setOnClickListener(this@RegisterActivity) }
        )
    }

    companion object {
        val ACCOUNT_TYPE = "no.kloktig.example"
        val AUTH_TOKEN_TYPE = "no.klokig.example.refresh"
        val KEY_USERID = "no.kloktig.example.userid"
    }
}