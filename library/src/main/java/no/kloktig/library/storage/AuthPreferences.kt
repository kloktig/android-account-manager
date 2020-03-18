import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle

class AuthPreferences(context: Context) : AuthLocalStorage {
    private val PREFS_NAME = "auth"
    private val KEY_ACCOUNT_NAME = "account_name"
    private val KEY_AUTH_TOKEN = "auth_token"
    private val KEY_USERID = "user_id"

    private val preferences: SharedPreferences

    override var refreshToken: String
        get() = KEY_AUTH_TOKEN.getString()
        set(authToken) = KEY_AUTH_TOKEN.setString(authToken)

    override var userId: String
        get() = KEY_USERID.getString()
        set(authToken) = KEY_USERID.setString(authToken)

    override var username: String
        get() = KEY_ACCOUNT_NAME.getString()
        set(accountName) = KEY_ACCOUNT_NAME.setString(accountName)

    private fun String.setString(value: String) = preferences.edit().putString(this, value).apply()
    private fun String.getString() = preferences.getString(this, "")!!

    override fun toString() = "$username - $userId:$refreshToken"

    init {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        fun from(context: Context, bundle: Bundle): AuthPreferences =
            AuthPreferences(context).apply {
                bundle.getString(AccountManager.KEY_ACCOUNT_NAME)?.let { username = it }
                bundle.getString(AccountManager.KEY_AUTHTOKEN)?.let { uidAndtoken ->
                    uidAndtoken.split(":").let {
                        userId = it[0]
                        refreshToken = it[1]
                    }
                }
            }
    }
}