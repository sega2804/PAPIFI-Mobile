package com.papifi.receive.ui

import androidx.lifecycle.viewModelScope
import com.papifi.common.onSuccess
import com.papifi.model.Currency
import com.papifi.receive.domain.ReceiveAccountDetails
import com.papifi.receive.domain.ReceiveRepository
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ReceiveUiState(
    val selectedCurrency: Currency = Currency.NGN,
    val isLoading: Boolean = false,
    val accountDetails: ReceiveAccountDetails? = null,
    val errorMessage: String? = null
) : UiState

sealed interface ReceiveIntent : ViewIntent {
    data class SelectCurrency(val currency: Currency) : ReceiveIntent
    data class CopyToClipboard(val label: String, val text: String) : ReceiveIntent
    data class ShareDetails(val details: ReceiveAccountDetails) : ReceiveIntent
}

sealed interface ReceiveEffect : ViewEffect {
    data class ShowMessage(val message: String) : ReceiveEffect
    data class ShareText(val text: String) : ReceiveEffect
}

class ReceiveViewModel(
    private val receiveRepository: ReceiveRepository
) : BaseViewModel<ReceiveIntent, ReceiveUiState, ReceiveEffect>(ReceiveUiState()) {

    init {
        loadDetails(Currency.NGN)
    }

    override fun handleIntent(intent: ReceiveIntent) {
        when (intent) {
            is ReceiveIntent.SelectCurrency -> {
                setState { copy(selectedCurrency = intent.currency) }
                loadDetails(intent.currency)
            }
            is ReceiveIntent.CopyToClipboard -> {
                setEffect { ReceiveEffect.ShowMessage("${intent.label} copied to clipboard") }
            }
            is ReceiveIntent.ShareDetails -> {
                val d = intent.details
                val shareText = buildString {
                    appendLine("Receive ${d.currency.code} Account Details")
                    appendLine("Bank: ${d.bankName}")
                    appendLine("Account Holder: ${d.accountName}")
                    appendLine("Account Number: ${d.accountNumber}")
                    if (!d.routingNumber.isNullOrBlank()) appendLine("Routing Number: ${d.routingNumber}")
                    if (!d.swiftCode.isNullOrBlank()) appendLine("SWIFT/BIC: ${d.swiftCode}")
                    if (!d.iban.isNullOrBlank()) appendLine("IBAN: ${d.iban}")
                }.trim()
                setEffect { ReceiveEffect.ShareText(shareText) }
            }
        }
    }

    private fun loadDetails(currency: Currency) {
        setState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            receiveRepository.getReceiveAccountDetails(currency).collectLatest { result ->
                result.onSuccess { details ->
                    setState { copy(accountDetails = details, isLoading = false) }
                }
            }
        }
    }
}
