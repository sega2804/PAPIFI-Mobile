package com.papifi.addmoney.ui.selector

import androidx.lifecycle.viewModelScope
import com.papifi.addmoney.domain.AddMoneyRepository
import com.papifi.addmoney.domain.FundingMethod
import com.papifi.common.onSuccess
import com.papifi.model.Currency
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class AddMoneySelectorUiState(
    val selectedCurrency: Currency = Currency.NGN,
    val availableMethods: List<FundingMethod> = emptyList(),
    val selectedMethod: FundingMethod? = FundingMethod.VIRTUAL_ACCOUNT,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface AddMoneySelectorIntent : ViewIntent {
    data class SelectCurrency(val currency: Currency) : AddMoneySelectorIntent
    data class SelectMethod(val method: FundingMethod) : AddMoneySelectorIntent
    data object ProceedClicked : AddMoneySelectorIntent
}

sealed interface AddMoneySelectorEffect : ViewEffect {
    data object NavigateToNgnBankDetails : AddMoneySelectorEffect
    data class NavigateToForeignDepositDetails(val currency: Currency) : AddMoneySelectorEffect
    data class ShowError(val message: String) : AddMoneySelectorEffect
}

class AddMoneySelectorViewModel(
    private val repository: AddMoneyRepository
) : BaseViewModel<AddMoneySelectorIntent, AddMoneySelectorUiState, AddMoneySelectorEffect>(
    AddMoneySelectorUiState()
) {

    init {
        loadFundingMethods(currentState.selectedCurrency)
    }

    override fun handleIntent(intent: AddMoneySelectorIntent) {
        when (intent) {
            is AddMoneySelectorIntent.SelectCurrency -> {
                setState { copy(selectedCurrency = intent.currency, errorMessage = null) }
                loadFundingMethods(intent.currency)
            }
            is AddMoneySelectorIntent.SelectMethod -> {
                setState { copy(selectedMethod = intent.method, errorMessage = null) }
            }
            AddMoneySelectorIntent.ProceedClicked -> proceedToFunding()
        }
    }

    private fun loadFundingMethods(currency: Currency) {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            repository.getFundingMethods(currency).collect { result ->
                result.onSuccess { methods ->
                    val defaultSelected = if (methods.contains(currentState.selectedMethod)) {
                        currentState.selectedMethod
                    } else {
                        methods.firstOrNull()
                    }
                    setState {
                        copy(
                            availableMethods = methods,
                            selectedMethod = defaultSelected,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun proceedToFunding() {
        val currency = currentState.selectedCurrency
        val method = currentState.selectedMethod

        if (method == null) {
            setEffect { AddMoneySelectorEffect.ShowError("Please select a funding method") }
            return
        }

        when (currency) {
            Currency.NGN -> {
                setEffect { AddMoneySelectorEffect.NavigateToNgnBankDetails }
            }
            Currency.USD, Currency.GBP, Currency.EUR -> {
                setEffect { AddMoneySelectorEffect.NavigateToForeignDepositDetails(currency) }
            }
        }
    }
}
