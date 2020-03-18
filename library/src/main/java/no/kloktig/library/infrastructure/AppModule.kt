package no.kloktig.library.infrastructure

import AuthLocalStorage
import AuthPreferences
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class AppModule() {
    fun create() = module {
        single<AuthLocalStorage> { AuthPreferences(get()) }
    }
}
