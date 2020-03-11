package no.kloktig.accountmanager

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle

class AccountAuthenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<String?>?,
        options: Bundle?
    ): Bundle {
        val intent = Intent(context, LoginActivity::class.java).apply {
            putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType)
            putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, authTokenType)
        }
        return Bundle().apply {
            putParcelable(AccountManager.KEY_INTENT, intent)
        }
    }

    override fun confirmCredentials(response: AccountAuthenticatorResponse, account: Account, bundle: Bundle) = Bundle()
    override fun editProperties(response: AccountAuthenticatorResponse, value: String) = Bundle()

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?, authTokenType: String?, options: Bundle?
    ): Bundle? {
        AccountManager.get(context)
            .peekAuthToken(account, authTokenType)?.let { token ->
                if (token.isNotEmpty()) {
                    return Bundle().apply {
                        putString(AccountManager.KEY_ACCOUNT_NAME, account?.name)
                        putString(AccountManager.KEY_ACCOUNT_TYPE, account?.type)
                        putString(AccountManager.KEY_AUTHTOKEN, token)
                    }
                }
            }
        throw Error("Token is empty! Response: $response, Account: $account, authTokenType: $authTokenType, options: $options")
    }

    override fun getAuthTokenLabel(authTokenType: String): String = ""
    override fun hasFeatures(response: AccountAuthenticatorResponse, account: Account, features: Array<String>) = Bundle()
    override fun updateCredentials(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, loginOptions: Bundle) = Bundle()
}