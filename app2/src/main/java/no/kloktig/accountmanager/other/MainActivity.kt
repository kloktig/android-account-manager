package no.kloktig.accountmanager.other

import AuthLocalStorage
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import no.kloktig.library.TokenHandler
import no.kloktig.library.infrastructure.AppModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : Activity() {
    private val register = 1
    private val update = 2
    private val tokenHandler = TokenHandler()
    private val storage: AuthLocalStorage by inject()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin{
            androidLogger()
            androidContext(this@MainActivity)
            modules(AppModule().create())
        }

        setContentView(R.layout.layout_main)

        val userInfo = R.id.user_name.getView<TextView>()
        val tokenInfo = R.id.refresh_token.getView<TextView>()
        val userId = R.id.user_id.getView<TextView>()

        val uiCallback = { storage: AuthLocalStorage ->
            userInfo.text = "APP2 - ${storage.username} "
            tokenInfo.text = storage.refreshToken
            userId.text = storage.userId
        }

        R.id.refresh_button.getView<Button>().setOnClickListener {
            tokenHandler.updateToken(this, update)
        }

        R.id.load_button.getView<Button>().setOnClickListener {
            tokenHandler.getToken(this, register)
            uiCallback.invoke(storage)
        }

        tokenHandler.getToken(this, register)

    }

    private fun <T> Int.getView() = findViewById<View>(this) as T
}