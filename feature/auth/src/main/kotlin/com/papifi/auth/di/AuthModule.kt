package com.papifi.auth.di

import com.papifi.auth.data.FakeAuthRepository
import com.papifi.auth.domain.AuthRepository
import com.papifi.auth.ui.login.LoginViewModel
import com.papifi.auth.ui.onboarding.OnboardingViewModel
import com.papifi.auth.ui.otp.OtpVerificationViewModel
import com.papifi.auth.ui.pin.CreatePinViewModel
import com.papifi.auth.ui.reset.ForgotPasswordViewModel
import com.papifi.auth.ui.signup.SignUpViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> { FakeAuthRepository() }
    viewModel { OnboardingViewModel() }
    viewModel { SignUpViewModel(get()) }
    viewModel { OtpVerificationViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { CreatePinViewModel(get()) }
}
