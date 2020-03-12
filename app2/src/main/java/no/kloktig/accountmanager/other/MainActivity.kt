package no.kloktig.accountmanager.other

import AuthLocalStorage
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import no.kloktig.library.TokenHandler

class MainActivity : Activity() {
    private val register = 1
    private val tokenHandler = TokenHandler()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        val userInfo = R.id.user_name.getView<TextView>()
        val tokenInfo = R.id.refresh_token.getView<TextView>()
        val userId = R.id.user_id.getView<TextView>()

        val uiCallback = { storage: AuthLocalStorage -> // TODO: Change to live data
            tokenInfo.text = "Token: ${storage.refreshToken}"
            userInfo.text = "User: ${storage.username} "
            userId.text = "UserId: ${storage.userId} "
        }

        tokenHandler.getToken(this, register, uiCallback)
    }
    private fun <T> Int.getView() = findViewById<View>(this) as T
}