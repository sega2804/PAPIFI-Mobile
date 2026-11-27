package com.papifi.activity.ui.receipt

import androidx.lifecycle.viewModelScope
import com.papifi.activity.domain.ActivityRepository
import com.papifi.activity.domain.TransactionActivityItem
import com.papifi.common.Result
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class TransactionReceiptUiState(
    val transaction: TransactionActivityItem? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface TransactionReceiptIntent : ViewIntent {
    data class LoadTransaction(val id: String) : TransactionReceiptIntent
    data class ShareReceipt(val transaction: TransactionActivityItem) : TransactionReceiptIntent
    data class ReportProblem(val reference: String) : TransactionReceiptIntent
}

sealed interface TransactionReceiptEffect : ViewEffect {
    data class ShareText(val text: String) : TransactionReceiptEffect
    data class NavigateToSupport(val reference: String) : TransactionReceiptEffect
    data class ShowMessage(val message: String) : TransactionReceiptEffect
}

class TransactionReceiptViewModel(
    private val activityRepository: ActivityRepository
) : BaseViewModel<TransactionReceiptIntent, TransactionReceiptUiState, TransactionReceiptEffect>(TransactionReceiptUiState()) {

    override fun handleIntent(intent: TransactionReceiptIntent) {
        when (intent) {
            is TransactionReceiptIntent.LoadTransaction -> {
                loadTransaction(intent.id)
            }
            is TransactionReceiptIntent.ShareReceipt -> {
                val t = intent.transaction
                val text = buildString {
                    appendLine("PAPIFI Transaction Receipt")
                    appendLine("Reference: ${t.reference}")
                    appendLine("Title: ${t.title}")
                    appendLine("Amount: ${t.amount.minorUnits} ${t.amount.currency.code}")
                    appendLine("Status: ${t.status.name}")
                    appendLine("Date: ${t.date}")
                }.trim()
                setEffect { TransactionReceiptEffect.ShareText(text) }
            }
            is TransactionReceiptIntent.ReportProblem -> {
                setEffect { TransactionReceiptEffect.NavigateToSupport(intent.reference) }
            }
        }
    }

    private fun loadTransaction(id: String) {
        setState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            activityRepository.getTransactionDetail(id).collectLatest { result ->
                when (result) {
                    is Result.Success -> setState { copy(transaction = result.data, isLoading = false) }
                    is Result.Error -> setState { copy(errorMessage = result.message, isLoading = false) }
                    is Result.Loading -> setState { copy(isLoading = true) }
                }
            }
        }
    }
}
