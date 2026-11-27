package com.papifi.auth.ui.login

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.papifi.auth.domain.AuthRepository
import com.papifi.common.onError
import com.papifi.common.onSuccess
import com.papifi.security.BiometricAuthenticator
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class LoginUiState(
    val emailOrPhone: String = "",
    val password: String = "",
    val canUseBiometrics: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface LoginIntent : ViewIntent {
    data class EmailOrPhoneChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object LoginClicked : LoginIntent
    data class BiometricLoginRequested(val activity: FragmentActivity) : LoginIntent
    data object BiometricSuccess : LoginIntent
    data object ForgotPasswordClicked : LoginIntent
    data object SignUpClicked : LoginIntent
}

sealed interface LoginEffect : ViewEffect {
    data object NavigateToHome : LoginEffect
    data object NavigateToCreatePin : LoginEffect
    data object NavigateToForgotPassword : LoginEffect
    data object NavigateToSignUp : LoginEffect
    data class ShowError(val message: String) : LoginEffect
}

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val biometricAuthenticator: BiometricAuthenticator
) : BaseViewModel<LoginIntent, LoginUiState, LoginEffect>(
    LoginUiState(canUseBiometrics = biometricAuthenticator.canAuthenticate())
) {

    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailOrPhoneChanged -> setState { copy(emailOrPhone = intent.value, errorMessage = null) }
            is LoginIntent.PasswordChanged -> setState { copy(password = intent.value, errorMessage = null) }
            LoginIntent.LoginClicked -> performLogin()
            is LoginIntent.BiometricLoginRequested -> performBiometricAuth(intent.activity)
            LoginIntent.BiometricSuccess -> performBiometricLogin()
            LoginIntent.ForgotPasswordClicked -> setEffect { LoginEffect.NavigateToForgotPassword }
            LoginIntent.SignUpClicked -> setEffect { LoginEffect.NavigateToSignUp }
        }
    }

    private fun performLogin() {
        val state = currentState
        if (state.emailOrPhone.isBlank() || state.password.isBlank()) {
            setState { copy(errorMessage = "Please enter your email/phone and password.") }
            return
        }

        setState { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            authRepository.login(state.emailOrPhone, state.password)
                .onSuccess { user ->
                    setState { copy(isLoading = false) }
                    if (user.pinCreated) {
                        setEffect { LoginEffect.NavigateToHome }
                    } else {
                        setEffect { LoginEffect.NavigateToCreatePin }
                    }
                }
                .onError { _, message ->
                    setState { copy(isLoading = false, errorMessage = message ?: "Invalid credentials.") }
                }
        }
    }

    private fun performBiometricAuth(activity: FragmentActivity) {
        biometricAuthenticator.authenticate(
            activity = activity,
            title = "Biometric Sign In",
            subtitle = "Log in to your PAPIFI account using biometrics",
            onSuccess = { sendIntent(LoginIntent.BiometricSuccess) },
            onError = { _, errString -> setState { copy(errorMessage = errString) } },
            onFailed = { setState { copy(errorMessage = "Biometric authentication failed.") } }
        )
    }

    private fun performBiometricLogin() {
        setState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.biometricLogin()
                .onSuccess { user ->
                    setState { copy(isLoading = false) }
                    if (user.pinCreated) {
                        setEffect { LoginEffect.NavigateToHome }
                    } else {
                        setEffect { LoginEffect.NavigateToCreatePin }
                    }
                }
                .onError { _, message ->
                    setState { copy(isLoading = false, errorMessage = message ?: "Biometric login failed.") }
                }
        }
    }
}
