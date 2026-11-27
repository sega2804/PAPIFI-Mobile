package com.papifi.kyc.ui.selfie

import androidx.lifecycle.viewModelScope
import com.papifi.common.onSuccess
import com.papifi.kyc.domain.KycRepository
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class KycSelfieUiState(
    val selfieUri: String? = null,
    val isSelfieCaptured: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface KycSelfieIntent : ViewIntent {
    data object TakeSelfieClicked : KycSelfieIntent
    data object SubmitVerificationClicked : KycSelfieIntent
}

sealed interface KycSelfieEffect : ViewEffect {
    data object NavigateToStatus : KycSelfieEffect
}

class KycSelfieViewModel(
    private val kycRepository: KycRepository
) : BaseViewModel<KycSelfieIntent, KycSelfieUiState, KycSelfieEffect>(KycSelfieUiState()) {

    override fun handleIntent(intent: KycSelfieIntent) {
        when (intent) {
            KycSelfieIntent.TakeSelfieClicked -> captureSelfie()
            KycSelfieIntent.SubmitVerificationClicked -> submitVerification()
        }
    }

    private fun captureSelfie() {
        setState {
            copy(
                selfieUri = "simulated_liveness_selfie.png",
                isSelfieCaptured = true,
                errorMessage = null
            )
        }
    }

    private fun submitVerification() {
        if (!currentState.isSelfieCaptured) {
            setState { copy(errorMessage = "Please take a selfie photo first.") }
            return
        }

        setState { copy(isLoading = true) }

        viewModelScope.launch {
            kycRepository.submitVerification(currentState.selfieUri).onSuccess {
                setState { copy(isLoading = false) }
                setEffect { KycSelfieEffect.NavigateToStatus }
            }
        }
    }
}
