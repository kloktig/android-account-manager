import android.content.Context
import android.content.SharedPreferences

class AuthPreferences(context: Context) {
    private val PREFS_NAME = "auth"
    private val KEY_ACCOUNT_NAME = "account_name"
    private val KEY_AUTH_TOKEN = "auth_token"
    private val preferences: SharedPreferences

    var authToken: String
        get() = KEY_AUTH_TOKEN.getString()
        set(authToken) = KEY_AUTH_TOKEN.setString(authToken)

    var username: String
        get() = KEY_ACCOUNT_NAME.getString()
        set(accountName) = KEY_ACCOUNT_NAME.setString(accountName)

    private fun String.setString(value: String) = preferences.edit().putString(this, value).apply()
    private fun String.getString() = preferences.getString(this, "")!!

    init {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}