package com.papifi.auth.ui.otp

import androidx.lifecycle.viewModelScope
import com.papifi.auth.domain.AuthRepository
import com.papifi.common.onError
import com.papifi.common.onSuccess
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OtpVerificationUiState(
    val emailOrPhone: String = "",
    val otpCode: String = "",
    val timerSeconds: Int = 30,
    val isResendEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface OtpVerificationIntent : ViewIntent {
    data class InitContact(val contact: String) : OtpVerificationIntent
    data class OtpChanged(val code: String) : OtpVerificationIntent
    data object ResendClicked : OtpVerificationIntent
    data object VerifyClicked : OtpVerificationIntent
}

sealed interface OtpVerificationEffect : ViewEffect {
    data object NavigateToCreatePin : OtpVerificationEffect
    data object NavigateToHome : OtpVerificationEffect
}

class OtpVerificationViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<OtpVerificationIntent, OtpVerificationUiState, OtpVerificationEffect>(
    OtpVerificationUiState()
) {
    private var timerJob: Job? = null

    init {
        startTimer()
    }

    override fun handleIntent(intent: OtpVerificationIntent) {
        when (intent) {
            is OtpVerificationIntent.InitContact -> {
                setState { copy(emailOrPhone = intent.contact) }
            }
            is OtpVerificationIntent.OtpChanged -> {
                val filtered = intent.code.filter { it.isDigit() }.take(6)
                setState { copy(otpCode = filtered, errorMessage = null) }
                if (filtered.length == 6) {
                    verifyOtp(filtered)
                }
            }
            OtpVerificationIntent.ResendClicked -> resendCode()
            OtpVerificationIntent.VerifyClicked -> verifyOtp(currentState.otpCode)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        setState { copy(timerSeconds = 30, isResendEnabled = false) }
        timerJob = viewModelScope.launch {
            while (currentState.timerSeconds > 0) {
                delay(1000)
                setState { copy(timerSeconds = timerSeconds - 1) }
            }
            setState { copy(isResendEnabled = true) }
        }
    }

    private fun resendCode() {
        if (!currentState.isResendEnabled) return
        viewModelScope.launch {
            authRepository.sendOtp(currentState.emailOrPhone)
            startTimer()
        }
    }

    private fun verifyOtp(code: String) {
        if (code.length != 6) {
            setState { copy(errorMessage = "Please enter a 6-digit OTP code.") }
            return
        }

        setState { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            authRepository.verifyOtp(code)
                .onSuccess { user ->
                    setState { copy(isLoading = false) }
                    if (user.pinCreated) {
                        setEffect { OtpVerificationEffect.NavigateToHome }
                    } else {
                        setEffect { OtpVerificationEffect.NavigateToCreatePin }
                    }
                }
                .onError { _, message ->
                    setState { copy(isLoading = false, errorMessage = message ?: "Invalid OTP code.") }
                }
        }
    }
}
