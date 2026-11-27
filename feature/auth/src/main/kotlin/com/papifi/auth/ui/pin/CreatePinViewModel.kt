package com.papifi.auth.ui.pin

import androidx.lifecycle.viewModelScope
import com.papifi.auth.domain.AuthRepository
import com.papifi.common.onError
import com.papifi.common.onSuccess
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class CreatePinUiState(
    val pin: String = "",
    val confirmPin: String = "",
    val isConfirming: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface CreatePinIntent : ViewIntent {
    data class NumberClicked(val digit: String) : CreatePinIntent
    data object DeleteClicked : CreatePinIntent
    data object ClearClicked : CreatePinIntent
}

sealed interface CreatePinEffect : ViewEffect {
    data object NavigateToHome : CreatePinEffect
}

class CreatePinViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<CreatePinIntent, CreatePinUiState, CreatePinEffect>(
    CreatePinUiState()
) {

    override fun handleIntent(intent: CreatePinIntent) {
        when (intent) {
            is CreatePinIntent.NumberClicked -> handleNumberInput(intent.digit)
            CreatePinIntent.DeleteClicked -> handleDelete()
            CreatePinIntent.ClearClicked -> setState { CreatePinUiState() }
        }
    }

    private fun handleNumberInput(digit: String) {
        val state = currentState
        if (state.isLoading) return

        if (!state.isConfirming) {
            if (state.pin.length < 4) {
                val newPin = state.pin + digit
                setState { copy(pin = newPin, errorMessage = null) }
                if (newPin.length == 4) {
                    setState { copy(isConfirming = true) }
                }
            }
        } else {
            if (state.confirmPin.length < 4) {
                val newConfirmPin = state.confirmPin + digit
                setState { copy(confirmPin = newConfirmPin, errorMessage = null) }
                if (newConfirmPin.length == 4) {
                    verifyAndSubmitPin(state.pin, newConfirmPin)
                }
            }
        }
    }

    private fun handleDelete() {
        val state = currentState
        if (state.isLoading) return

        if (state.isConfirming) {
            if (state.confirmPin.isNotEmpty()) {
                setState { copy(confirmPin = confirmPin.dropLast(1), errorMessage = null) }
            } else {
                setState { copy(isConfirming = false, pin = pin.dropLast(1), errorMessage = null) }
            }
        } else {
            if (state.pin.isNotEmpty()) {
                setState { copy(pin = pin.dropLast(1), errorMessage = null) }
            }
        }
    }

    private fun verifyAndSubmitPin(pin: String, confirmPin: String) {
        if (pin != confirmPin) {
            setState {
                copy(
                    pin = "",
                    confirmPin = "",
                    isConfirming = false,
                    errorMessage = "PINs do not match. Please try again."
                )
            }
            return
        }

        setState { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            authRepository.createPin(pin)
                .onSuccess {
                    setState { copy(isLoading = false) }
                    setEffect { CreatePinEffect.NavigateToHome }
                }
                .onError { _, message ->
                    setState { copy(isLoading = false, errorMessage = message ?: "Failed to create PIN.") }
                }
        }
    }
}
