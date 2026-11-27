package com.papifi

import android.app.Application
import com.papifi.auth.di.authModule
import com.papifi.kyc.di.kycModule
import com.papifi.profile.di.profileModule
import com.papifi.security.AndroidBiometricAuthenticator
import com.papifi.security.AndroidSecureStore
import com.papifi.security.BiometricAuthenticator
import com.papifi.security.SecureStore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class PapifiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PapifiApplication)
            modules(appModule, authModule, profileModule, kycModule)
        }
    }

    private val appModule = module {
        single<SecureStore> { AndroidSecureStore(get()) }
        single<BiometricAuthenticator> { AndroidBiometricAuthenticator(get()) }
    }
}
