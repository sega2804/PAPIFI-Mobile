package com.papifi.send.ui.review

import androidx.lifecycle.viewModelScope
import com.papifi.common.IdempotencyKeyGenerator
import com.papifi.common.Result
import com.papifi.model.Currency
import com.papifi.model.Money
import com.papifi.send.domain.Beneficiary
import com.papifi.send.domain.SendRepository
import com.papifi.send.domain.TransferRequest
import com.papifi.send.domain.TransferRoute
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SendReviewUiState(
    val isLoading: Boolean = false,
    val beneficiary: Beneficiary? = null,
    val route: TransferRoute = TransferRoute.LOCAL_BANK,
    val amountMinor: Long = 0L,
    val note: String = "",
    val feeMinor: Long = 5000L, // ₦50.00 fee
    val exchangeRate: String = "1.00 NGN = 1.00 NGN",
    val idempotencyKey: String = "",
    val isPinDialogVisible: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface SendReviewIntent : ViewIntent {
    data class InitReview(val recipientId: String, val route: TransferRoute, val amountMinor: Long, val note: String) : SendReviewIntent
    data object ConfirmAndPayClicked : SendReviewIntent
    data class PinEntered(val pin: String) : SendReviewIntent
    data class TogglePinDialog(val visible: Boolean) : SendReviewIntent
}

sealed interface SendReviewEffect : ViewEffect {
    data class NavigateToStatus(val transactionReference: String) : SendReviewEffect
    data class ShowError(val message: String) : SendReviewEffect
}

class SendReviewViewModel(
    private val repository: SendRepository,
    private val idempotencyKeyGenerator: IdempotencyKeyGenerator
) : BaseViewModel<SendReviewIntent, SendReviewUiState, SendReviewEffect>(SendReviewUiState()) {

    override fun handleIntent(intent: SendReviewIntent) {
        when (intent) {
            is SendReviewIntent.InitReview -> {
                val key = idempotencyKeyGenerator.generateKey()
                setState {
                    copy(
                        route = intent.route,
                        amountMinor = intent.amountMinor,
                        note = intent.note,
                        idempotencyKey = key
                    )
                }
                loadBeneficiary(intent.recipientId)
            }
            SendReviewIntent.ConfirmAndPayClicked -> {
                setState { copy(isPinDialogVisible = true) }
            }
            is SendReviewIntent.TogglePinDialog -> {
                setState { copy(isPinDialogVisible = intent.visible) }
            }
            is SendReviewIntent.PinEntered -> {
                setState { copy(isPinDialogVisible = false) }
                executeTransfer(intent.pin)
            }
        }
    }

    private fun loadBeneficiary(recipientId: String) {
        viewModelScope.launch {
            repository.getBeneficiaries().collectLatest { result ->
                if (result is Result.Success) {
                    val beneficiary = result.data.find { it.id == recipientId } ?: result.data.firstOrNull()
                    setState { copy(beneficiary = beneficiary) }
                }
            }
        }
    }

    private fun executeTransfer(pin: String) {
        val beneficiary = currentState.beneficiary ?: return
        val request = TransferRequest(
            recipientId = beneficiary.id,
            route = currentState.route,
            amount = Money(currentState.amountMinor, beneficiary.currency),
            note = currentState.note,
            idempotencyKey = currentState.idempotencyKey
        )
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            repository.executeTransfer(request, pin).collectLatest { result ->
                when (result) {
                    is Result.Loading -> setState { copy(isLoading = true) }
                    is Result.Success -> {
                        setState { copy(isLoading = false) }
                        setEffect { SendReviewEffect.NavigateToStatus(result.data.transactionReference) }
                    }
                    is Result.Error -> {
                        setState { copy(isLoading = false, errorMessage = result.message) }
                        setEffect { SendReviewEffect.ShowError(result.message ?: "Transfer failed") }
                    }
                }
            }
        }
    }
}
