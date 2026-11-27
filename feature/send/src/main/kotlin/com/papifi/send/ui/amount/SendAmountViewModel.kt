package com.papifi.send.ui.amount

import com.papifi.model.Currency
import com.papifi.send.domain.TransferRoute
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent

data class SendAmountUiState(
    val route: TransferRoute = TransferRoute.LOCAL_BANK,
    val amountString: String = "",
    val note: String = "",
    val availableBalanceMinor: Long = 125000000L, // 1,250,000.00 NGN
    val currency: Currency = Currency.NGN,
    val errorMessage: String? = null
) : UiState

sealed interface SendAmountIntent : ViewIntent {
    data class AmountChanged(val amount: String) : SendAmountIntent
    data class NoteChanged(val note: String) : SendAmountIntent
    data object ContinueClicked : SendAmountIntent
}

sealed interface SendAmountEffect : ViewEffect {
    data class NavigateToReview(val route: TransferRoute, val amountMinor: Long, val note: String) : SendAmountEffect
    data class ShowError(val message: String) : SendAmountEffect
}

class SendAmountViewModel : BaseViewModel<SendAmountIntent, SendAmountUiState, SendAmountEffect>(SendAmountUiState()) {

    override fun handleIntent(intent: SendAmountIntent) {
        when (intent) {
            is SendAmountIntent.AmountChanged -> {
                val filtered = intent.amount.filter { it.isDigit() || it == '.' }
                setState { copy(amountString = filtered, errorMessage = null) }
            }
            is SendAmountIntent.NoteChanged -> {
                setState { copy(note = intent.note) }
            }
            SendAmountIntent.ContinueClicked -> {
                validateAndProceed()
            }
        }
    }

    private fun validateAndProceed() {
        val amountDouble = currentState.amountString.toDoubleOrNull() ?: 0.0
        if (amountDouble <= 0.0) {
            setState { copy(errorMessage = "Please enter a valid amount") }
            return
        }
        val minorUnits = (amountDouble * 100.0).toLong()
        if (minorUnits > currentState.availableBalanceMinor) {
            setState { copy(errorMessage = "Amount exceeds available balance") }
            return
        }
        setEffect {
            SendAmountEffect.NavigateToReview(
                route = currentState.route,
                amountMinor = minorUnits,
                note = currentState.note
            )
        }
    }
}
