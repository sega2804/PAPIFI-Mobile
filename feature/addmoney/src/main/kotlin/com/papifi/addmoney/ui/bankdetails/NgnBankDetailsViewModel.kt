package com.papifi.addmoney.ui.bankdetails

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

data class NgnBankDetailsUiState(
    val fundingDetails: FundingDetails? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) : UiState

sealed interface NgnBankDetailsIntent : ViewIntent {
    data object LoadDetails : NgnBankDetailsIntent
    data object TransferMadeClicked : NgnBankDetailsIntent
}

sealed interface NgnBankDetailsEffect : ViewEffect {
    data object NavigateToPending : NgnBankDetailsEffect
    data class ShowToast(val message: String) : NgnBankDetailsEffect
}

class NgnBankDetailsViewModel(
    private val repository: AddMoneyRepository
) : BaseViewModel<NgnBankDetailsIntent, NgnBankDetailsUiState, NgnBankDetailsEffect>(
    NgnBankDetailsUiState()
) {

    init {
        loadDetails()
    }

    override fun handleIntent(intent: NgnBankDetailsIntent) {
        when (intent) {
            NgnBankDetailsIntent.LoadDetails -> loadDetails()
            NgnBankDetailsIntent.TransferMadeClicked -> {
                setEffect { NgnBankDetailsEffect.NavigateToPending }
            }
        }
    }

    private fun loadDetails() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            repository.getFundingDetails(Currency.NGN, FundingMethod.VIRTUAL_ACCOUNT).collect { result ->
                result.onSuccess { details ->
                    setState { copy(fundingDetails = details, isLoading = false) }
                }
            }
        }
    }
}
