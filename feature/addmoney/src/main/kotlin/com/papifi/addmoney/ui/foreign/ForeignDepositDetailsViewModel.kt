package com.papifi.addmoney.ui.foreign

import androidx.lifecycle.viewModelScope
import com.papifi.addmoney.domain.AddMoneyRepository
import com.papifi.addmoney.domain.FundingDetails
import com.papifi.addmoney.domain.FundingMethod
import com.papifi.common.onSuccess
import com.papifi.model.Currency
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class ForeignDepositDetailsUiState(
    val currency: Currency = Currency.USD,
    val fundingDetails: FundingDetails? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) : UiState

sealed interface ForeignDepositDetailsIntent : ViewIntent {
    data class LoadDetails(val currency: Currency) : ForeignDepositDetailsIntent
    data object CopyAllDetailsClicked : ForeignDepositDetailsIntent
    data object DoneClicked : ForeignDepositDetailsIntent
}

sealed interface ForeignDepositDetailsEffect : ViewEffect {
    data object NavigateToPending : ForeignDepositDetailsEffect
    data class ShowToast(val message: String) : ForeignDepositDetailsEffect
    data class CopyAllToClipboard(val text: String) : ForeignDepositDetailsEffect
}

class ForeignDepositDetailsViewModel(
    private val repository: AddMoneyRepository
) : BaseViewModel<ForeignDepositDetailsIntent, ForeignDepositDetailsUiState, ForeignDepositDetailsEffect>(
    ForeignDepositDetailsUiState()
) {

    override fun handleIntent(intent: ForeignDepositDetailsIntent) {
        when (intent) {
            is ForeignDepositDetailsIntent.LoadDetails -> loadDetails(intent.currency)
            ForeignDepositDetailsIntent.CopyAllDetailsClicked -> copyAllDetails()
            ForeignDepositDetailsIntent.DoneClicked -> {
                setEffect { ForeignDepositDetailsEffect.NavigateToPending }
            }
        }
    }

    fun loadDetails(currency: Currency) {
        setState { copy(currency = currency, isLoading = true) }
        viewModelScope.launch {
            repository.getFundingDetails(currency, FundingMethod.FOREIGN_WIRE).collect { result ->
                result.onSuccess { details ->
                    setState { copy(fundingDetails = details, isLoading = false) }
                }
            }
        }
    }

    private fun copyAllDetails() {
        val details = currentState.fundingDetails ?: return
        val text = buildString {
            appendLine("Bank Name: ${details.bankName}")
            appendLine("Account Name: ${details.accountName}")
            appendLine("Account Number: ${details.accountNumber}")
            details.iban?.let { appendLine("IBAN: $it") }
            details.swiftCode?.let { appendLine("SWIFT/BIC: $it") }
            details.routingNumber?.let { appendLine("Routing Number: $it") }
            details.sortCode?.let { appendLine("Sort Code: $it") }
            details.bankAddress?.let { appendLine("Bank Address: $it") }
            details.referenceCode?.let { appendLine("Reference Code: $it") }
        }
        setEffect { ForeignDepositDetailsEffect.CopyAllToClipboard(text) }
        setEffect { ForeignDepositDetailsEffect.ShowToast("All details copied to clipboard") }
    }
}
