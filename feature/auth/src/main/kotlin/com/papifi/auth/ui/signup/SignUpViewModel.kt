package com.papifi.auth.ui.signup

import androidx.lifecycle.viewModelScope
import com.papifi.auth.domain.AuthRepository
import com.papifi.common.onError
import com.papifi.common.onSuccess
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isTermsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface SignUpIntent : ViewIntent {
    data class NameChanged(val name: String) : SignUpIntent
    data class EmailChanged(val email: String) : SignUpIntent
    data class PasswordChanged(val password: String) : SignUpIntent
    data class TermsToggled(val accepted: Boolean) : SignUpIntent
    data object SignUpClicked : SignUpIntent
    data object LoginClicked : SignUpIntent
}

sealed interface SignUpEffect : ViewEffect {
    data class NavigateToOtp(val email: String) : SignUpEffect
    data object NavigateToLogin : SignUpEffect
}

class SignUpViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<SignUpIntent, SignUpUiState, SignUpEffect>(SignUpUiState()) {

    override fun handleIntent(intent: SignUpIntent) {
        when (intent) {
            is SignUpIntent.NameChanged -> setState { copy(name = intent.name, errorMessage = null) }
            is SignUpIntent.EmailChanged -> setState { copy(email = intent.email, errorMessage = null) }
            is SignUpIntent.PasswordChanged -> setState { copy(password = intent.password, errorMessage = null) }
            is SignUpIntent.TermsToggled -> setState { copy(isTermsAccepted = intent.accepted, errorMessage = null) }
            SignUpIntent.SignUpClicked -> performSignUp()
            SignUpIntent.LoginClicked -> setEffect { SignUpEffect.NavigateToLogin }
        }
    }

    private fun performSignUp() {
        val state = currentState
        if (state.name.isBlank()) {
            setState { copy(errorMessage = "Please enter your full name.") }
            return
        }
        if (state.email.isBlank() || !state.email.contains("@")) {
            setState { copy(errorMessage = "Please enter a valid email address.") }
            return
        }
        if (state.password.length < 6) {
            setState { copy(errorMessage = "Password must be at least 6 characters.") }
            return
        }
        if (!state.isTermsAccepted) {
            setState { copy(errorMessage = "You must accept the terms and conditions.") }
            return
        }

        setState { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            authRepository.signup(state.name, state.email, state.password)
                .onSuccess { user ->
                    setState { copy(isLoading = false) }
                    setEffect { SignUpEffect.NavigateToOtp(user.email) }
                }
                .onError { _, message ->
                    setState { copy(isLoading = false, errorMessage = message ?: "Sign up failed.") }
                }
        }
    }
}
