package com.papifi.send.ui.status

import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent

data class SendStatusUiState(
    val transactionReference: String = "",
    val amountString: String = "₦50,000.00",
    val dateString: String = "Today, 14:32"
) : UiState

sealed interface SendStatusIntent : ViewIntent {
    data class Init(val reference: String) : SendStatusIntent
    data object ViewReceiptClicked : SendStatusIntent
    data object ReturnToWalletClicked : SendStatusIntent
}

sealed interface SendStatusEffect : ViewEffect {
    data object ReturnToWallet : SendStatusEffect
    data class ShowReceipt(val reference: String) : SendStatusEffect
}

class SendStatusViewModel : BaseViewModel<SendStatusIntent, SendStatusUiState, SendStatusEffect>(SendStatusUiState()) {

    override fun handleIntent(intent: SendStatusIntent) {
        when (intent) {
            is SendStatusIntent.Init -> setState { copy(transactionReference = intent.reference) }
            SendStatusIntent.ViewReceiptClicked -> setEffect { SendStatusEffect.ShowReceipt(currentState.transactionReference) }
            SendStatusIntent.ReturnToWalletClicked -> setEffect { SendStatusEffect.ReturnToWallet }
        }
    }
}
