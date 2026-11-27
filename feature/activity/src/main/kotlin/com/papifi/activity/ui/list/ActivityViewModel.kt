package com.papifi.activity.ui.list

import androidx.lifecycle.viewModelScope
import com.papifi.activity.domain.ActivityRepository
import com.papifi.activity.domain.TransactionActivityItem
import com.papifi.common.Result
import com.papifi.model.Currency
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ActivityUiState(
    val transactions: List<TransactionActivityItem> = emptyList(),
    val isLoading: Boolean = false,
    val selectedFilterType: String? = "ALL", // ALL, SEND, RECEIVE, CONVERT, ADD
    val selectedCurrency: Currency? = null,
    val searchQuery: String = "",
    val errorMessage: String? = null
) : UiState

sealed interface ActivityIntent : ViewIntent {
    data class SetFilterType(val type: String?) : ActivityIntent
    data class SetCurrency(val currency: Currency?) : ActivityIntent
    data class SetSearchQuery(val query: String) : ActivityIntent
    data class TransactionClicked(val txId: String) : ActivityIntent
    data class Refresh(val type: String?, val currency: Currency?) : ActivityIntent
}

sealed interface ActivityEffect : ViewEffect {
    data class NavigateToReceipt(val txId: String) : ActivityEffect
}

class ActivityViewModel(
    private val activityRepository: ActivityRepository
) : BaseViewModel<ActivityIntent, ActivityUiState, ActivityEffect>(ActivityUiState()) {

    init {
        loadTransactions(currentState.selectedFilterType, currentState.selectedCurrency)
    }

    override fun handleIntent(intent: ActivityIntent) {
        when (intent) {
            is ActivityIntent.SetFilterType -> {
                setState { copy(selectedFilterType = intent.type) }
                loadTransactions(intent.type, currentState.selectedCurrency)
            }
            is ActivityIntent.SetCurrency -> {
                setState { copy(selectedCurrency = intent.currency) }
                loadTransactions(currentState.selectedFilterType, intent.currency)
            }
            is ActivityIntent.SetSearchQuery -> {
                setState { copy(searchQuery = intent.query) }
            }
            is ActivityIntent.TransactionClicked -> {
                setEffect { ActivityEffect.NavigateToReceipt(intent.txId) }
            }
            is ActivityIntent.Refresh -> {
                loadTransactions(intent.type, intent.currency)
            }
        }
    }

    private fun loadTransactions(filterType: String?, currency: Currency?) {
        setState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            activityRepository.getTransactions(filterType, currency).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        setState { copy(transactions = result.data, isLoading = false) }
                    }
                    is Result.Error -> {
                        setState { copy(errorMessage = result.message, isLoading = false) }
                    }
                    is Result.Loading -> {
                        setState { copy(isLoading = true) }
                    }
                }
            }
        }
    }
}
