package com.papifi.kyc.ui.status

import androidx.lifecycle.viewModelScope
import com.papifi.common.onSuccess
import com.papifi.kyc.domain.KycRepository
import com.papifi.model.KycStatus
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class KycStatusUiState(
    val kycStatus: KycStatus = KycStatus.PENDING,
    val rejectionReason: String? = null,
    val isLoading: Boolean = false
) : UiState

sealed interface KycStatusIntent : ViewIntent {
    data object LoadStatus : KycStatusIntent
    data object ReturnHomeClicked : KycStatusIntent
    data object ResubmitClicked : KycStatusIntent
}

sealed interface KycStatusEffect : ViewEffect {
    data object NavigateToHome : KycStatusEffect
    data object NavigateToStart : KycStatusEffect
}

class KycStatusViewModel(
    private val kycRepository: KycRepository
) : BaseViewModel<KycStatusIntent, KycStatusUiState, KycStatusEffect>(KycStatusUiState()) {

    init {
        sendIntent(KycStatusIntent.LoadStatus)
    }

    override fun handleIntent(intent: KycStatusIntent) {
        when (intent) {
            KycStatusIntent.LoadStatus -> loadStatus()
            KycStatusIntent.ReturnHomeClicked -> setEffect { KycStatusEffect.NavigateToHome }
            KycStatusIntent.ResubmitClicked -> resubmit()
        }
    }

    private fun loadStatus() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            kycRepository.getKycStatus().collect { result ->
                result.onSuccess { status ->
                    val reason = if (status == KycStatus.REJECTED) {
                        "Uploaded ID document image was blurred and unreadable. Please upload a clear photo of your ID."
                    } else null

                    setState {
                        copy(
                            kycStatus = status,
                            rejectionReason = reason,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun resubmit() {
        viewModelScope.launch {
            kycRepository.resetVerification().onSuccess {
                setEffect { KycStatusEffect.NavigateToStart }
            }
        }
    }
}
