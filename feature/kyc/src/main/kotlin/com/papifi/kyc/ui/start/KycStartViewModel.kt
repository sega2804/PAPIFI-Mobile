package com.papifi.kyc.ui.start

import androidx.lifecycle.viewModelScope
import com.papifi.common.onSuccess
import com.papifi.kyc.domain.KycRepository
import com.papifi.model.KycStatus
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class KycStartUiState(
    val currentStatus: KycStatus = KycStatus.NOT_STARTED,
    val isLoading: Boolean = false
) : UiState

sealed interface KycStartIntent : ViewIntent {
    data object StartVerificationClicked : KycStartIntent
}

sealed interface KycStartEffect : ViewEffect {
    data object NavigateToDocumentUpload : KycStartEffect
    data object NavigateToStatus : KycStartEffect
}

class KycStartViewModel(
    private val kycRepository: KycRepository
) : BaseViewModel<KycStartIntent, KycStartUiState, KycStartEffect>(KycStartUiState()) {

    init {
        viewModelScope.launch {
            kycRepository.getKycStatus().collect { result ->
                result.onSuccess { status ->
                    setState { copy(currentStatus = status) }
                }
            }
        }
    }

    override fun handleIntent(intent: KycStartIntent) {
        when (intent) {
            KycStartIntent.StartVerificationClicked -> {
                if (currentState.currentStatus == KycStatus.PENDING || currentState.currentStatus == KycStatus.VERIFIED) {
                    setEffect { KycStartEffect.NavigateToStatus }
                } else {
                    setEffect { KycStartEffect.NavigateToDocumentUpload }
                }
            }
        }
    }
}
