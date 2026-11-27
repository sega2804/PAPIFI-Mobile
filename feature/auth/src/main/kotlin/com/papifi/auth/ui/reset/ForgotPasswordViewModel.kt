package com.papifi.auth.ui.reset

import androidx.lifecycle.viewModelScope
import com.papifi.auth.domain.AuthRepository
import com.papifi.common.onError
import com.papifi.common.onSuccess
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccessSent: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface ForgotPasswordIntent : ViewIntent {
    data class EmailChanged(val email: String) : ForgotPasswordIntent
    data object SendResetLinkClicked : ForgotPasswordIntent
    data object BackToLoginClicked : ForgotPasswordIntent
}

sealed interface ForgotPasswordEffect : ViewEffect {
    data object NavigateBack : ForgotPasswordEffect
}

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<ForgotPasswordIntent, ForgotPasswordUiState, ForgotPasswordEffect>(
    ForgotPasswordUiState()
) {

    override fun handleIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.EmailChanged -> setState { copy(email = intent.email, errorMessage = null) }
            ForgotPasswordIntent.SendResetLinkClicked -> sendResetLink()
            ForgotPasswordIntent.BackToLoginClicked -> setEffect { ForgotPasswordEffect.NavigateBack }
        }
    }

    private fun sendResetLink() {
        val email = currentState.email
        if (email.isBlank() || !email.contains("@")) {
            setState { copy(errorMessage = "Please enter a valid email address.") }
            return
        }

        setState { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            authRepository.resetPassword(email)
                .onSuccess {
                    setState { copy(isLoading = false, isSuccessSent = true) }
                }
                .onError { _, message ->
                    setState { copy(isLoading = false, errorMessage = message ?: "Failed to send reset link.") }
                }
        }
    }
}
