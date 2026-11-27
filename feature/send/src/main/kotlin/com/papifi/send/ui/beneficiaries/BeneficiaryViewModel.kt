package com.papifi.send.ui.beneficiaries

import androidx.lifecycle.viewModelScope
import com.papifi.common.Result
import com.papifi.model.Currency
import com.papifi.send.domain.Beneficiary
import com.papifi.send.domain.SendRepository
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

data class BeneficiaryUiState(
    val isLoading: Boolean = false,
    val beneficiaries: List<Beneficiary> = emptyList(),
    val searchQuery: String = "",
    val isAddDialogVisible: Boolean = false,
    val selectedBeneficiary: Beneficiary? = null,
    val errorMessage: String? = null
) : UiState

sealed interface BeneficiaryIntent : ViewIntent {
    data class SearchQueryChanged(val query: String) : BeneficiaryIntent
    data class ToggleAddDialog(val visible: Boolean) : BeneficiaryIntent
    data class AddBeneficiary(val name: String, val bankName: String, val accountNumber: String, val currency: Currency) : BeneficiaryIntent
    data class SelectBeneficiary(val beneficiary: Beneficiary) : BeneficiaryIntent
}

sealed interface BeneficiaryEffect : ViewEffect {
    data class NavigateToDestination(val beneficiaryId: String) : BeneficiaryEffect
    data class ShowError(val message: String) : BeneficiaryEffect
}

class BeneficiaryViewModel(
    private val repository: SendRepository
) : BaseViewModel<BeneficiaryIntent, BeneficiaryUiState, BeneficiaryEffect>(BeneficiaryUiState()) {

    init {
        loadBeneficiaries()
    }

    override fun handleIntent(intent: BeneficiaryIntent) {
        when (intent) {
            is BeneficiaryIntent.SearchQueryChanged -> {
                setState { copy(searchQuery = intent.query) }
            }
            is BeneficiaryIntent.ToggleAddDialog -> {
                setState { copy(isAddDialogVisible = intent.visible, errorMessage = null) }
            }
            is BeneficiaryIntent.AddBeneficiary -> {
                addBeneficiary(intent.name, intent.bankName, intent.accountNumber, intent.currency)
            }
            is BeneficiaryIntent.SelectBeneficiary -> {
                setState { copy(selectedBeneficiary = intent.beneficiary) }
                setEffect { BeneficiaryEffect.NavigateToDestination(intent.beneficiary.id) }
            }
        }
    }

    private fun loadBeneficiaries() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            repository.getBeneficiaries().collectLatest { result ->
                when (result) {
                    is Result.Loading -> setState { copy(isLoading = true) }
                    is Result.Success -> setState { copy(isLoading = false, beneficiaries = result.data) }
                    is Result.Error -> setState { copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    private fun addBeneficiary(name: String, bankName: String, accountNumber: String, currency: Currency) {
        if (name.isBlank() || bankName.isBlank() || accountNumber.isBlank()) {
            setState { copy(errorMessage = "All fields are required") }
            return
        }
        val newBeneficiary = Beneficiary(
            id = UUID.randomUUID().toString(),
            name = name,
            bankName = bankName,
            accountNumber = accountNumber,
            currency = currency,
            avatarUrl = null
        )
        viewModelScope.launch {
            repository.addBeneficiary(newBeneficiary).collectLatest { result ->
                if (result is Result.Success) {
                    setState { copy(isAddDialogVisible = false) }
                    loadBeneficiaries()
                } else if (result is Result.Error) {
                    setState { copy(errorMessage = result.message) }
                }
            }
        }
    }
}
