package com.papifi.support.ui

import androidx.lifecycle.viewModelScope
import com.papifi.common.Result
import com.papifi.support.domain.SupportRepository
import com.papifi.support.domain.SupportTicket
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SupportUiState(
    val category: String = "Transaction Issue",
    val subject: String = "",
    val description: String = "",
    val transactionReference: String = "",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface SupportIntent : ViewIntent {
    data class SetCategory(val category: String) : SupportIntent
    data class SetSubject(val subject: String) : SupportIntent
    data class SetDescription(val description: String) : SupportIntent
    data class SetTransactionReference(val reference: String) : SupportIntent
    data object SubmitTicket : SupportIntent
    data object ResetState : SupportIntent
}

sealed interface SupportEffect : ViewEffect {
    data class ShowMessage(val message: String) : SupportEffect
    data object TicketSubmitted : SupportEffect
}

class SupportViewModel(
    private val supportRepository: SupportRepository
) : BaseViewModel<SupportIntent, SupportUiState, SupportEffect>(SupportUiState()) {

    override fun handleIntent(intent: SupportIntent) {
        when (intent) {
            is SupportIntent.SetCategory -> setState { copy(category = intent.category) }
            is SupportIntent.SetSubject -> setState { copy(subject = intent.subject) }
            is SupportIntent.SetDescription -> setState { copy(description = intent.description) }
            is SupportIntent.SetTransactionReference -> setState { copy(transactionReference = intent.reference) }
            is SupportIntent.SubmitTicket -> submitTicket()
            is SupportIntent.ResetState -> setState { SupportUiState() }
        }
    }

    private fun submitTicket() {
        val state = currentState
        if (state.subject.isBlank() || state.description.isBlank()) {
            setEffect { SupportEffect.ShowMessage("Please fill in all required fields") }
            return
        }

        setState { copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            val ticket = SupportTicket(
                category = state.category,
                subject = state.subject,
                description = state.description,
                transactionReference = state.transactionReference.takeIf { it.isNotBlank() }
            )
            supportRepository.submitTicket(ticket).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        setState { copy(isSubmitting = false, isSuccess = true) }
                        setEffect { SupportEffect.TicketSubmitted }
                    }
                    is Result.Error -> {
                        setState { copy(isSubmitting = false, errorMessage = result.message) }
                        setEffect { SupportEffect.ShowMessage(result.message ?: "Failed to submit ticket") }
                    }
                    is Result.Loading -> {
                        setState { copy(isSubmitting = true) }
                    }
                }
            }
        }
    }
}
