package com.papifi.addmoney.ui.pending

import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent

data class FundingPendingUiState(
    val isProcessing: Boolean = true
) : UiState

sealed interface FundingPendingIntent : ViewIntent {
    data object ReturnToWalletClicked : FundingPendingIntent
}

sealed interface FundingPendingEffect : ViewEffect {
    data object NavigateToWallet : FundingPendingEffect
}

class FundingPendingViewModel : BaseViewModel<FundingPendingIntent, FundingPendingUiState, FundingPendingEffect>(
    FundingPendingUiState()
) {
    override fun handleIntent(intent: FundingPendingIntent) {
        when (intent) {
            FundingPendingIntent.ReturnToWalletClicked -> {
                setEffect { FundingPendingEffect.NavigateToWallet }
            }
        }
    }
}
