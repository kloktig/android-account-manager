package no.kloktig.accountmanager

import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import kotlin.system.exitProcess

class LoginActivity : AccountAuthenticatorActivity(), View.OnClickListener {
    private lateinit var am: AccountManager
    private lateinit var email: String
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
        email = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) ?: ""
        viewParams = setupView()
    }

    override fun onClick(view: View) {
        if (view == viewParams.loginButton)
            attemptLogin()
    }

    private fun attemptLogin() {
        email = viewParams.email.text.toString()
        password = viewParams.password.text.toString()

        val authToken = viewParams.password.text.toString()
        val account = Account(email, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE))

        am.addAccountExplicitly(account, null, null)
        am.setAuthToken(account, AUTH_TOKEN_TYPE, authToken)

        setAccountAuthenticatorResult(intent.extras)
        setResult(RESULT_OK, intent)

        exitProcess(0)
    }

    private fun <T> Int.get() = findViewById<View>(this) as T

    private fun setupView(): ViewParameters {
        return ViewParameters(
            email = R.id.email.get<EditText>().apply { setText(email) },
            password = R.id.password.get(),
            loginButton = R.id.login_btn.get<Button>().apply { setOnClickListener(this@LoginActivity) }
        )
    }

    companion object {
        val ACCOUNT_TYPE = "no.kloktig.example"
        val AUTH_TOKEN_TYPE = "no.klokig.example.refresh"
    }
}