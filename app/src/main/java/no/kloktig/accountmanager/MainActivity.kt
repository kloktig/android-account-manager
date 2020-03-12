package no.kloktig.accountmanager

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import no.kloktig.library.TokenHandler

class MainActivity : Activity() {
    private val register = 1
    private val tokenHandler = TokenHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        val userInfo = android.R.id.text1.getView<TextView>()
        val tokenInfo = android.R.id.text2.getView<TextView>()

        val uiCallback = { user: String, token: String -> // TODO: Change to live data
            tokenInfo.text = "Token: $token"
            userInfo.text = "User: $user "
        }

        tokenHandler.getToken(this, register, uiCallback)
    }
    private fun <T> Int.getView() = findViewById<View>(this) as T
}