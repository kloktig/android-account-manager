import android.content.Context
import android.content.SharedPreferences

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

    init {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}