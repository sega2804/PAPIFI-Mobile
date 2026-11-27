package com.papifi.wallet.ui

import androidx.lifecycle.viewModelScope
import com.papifi.common.onSuccess
import com.papifi.model.Currency
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import com.papifi.wallet.domain.ExchangeRate
import com.papifi.wallet.domain.TransactionSummary
import com.papifi.wallet.domain.WalletBalance
import com.papifi.wallet.domain.WalletRepository
import kotlinx.coroutines.launch

data class WalletUiState(
    val selectedCurrency: Currency = Currency.NGN,
    val isBalanceVisible: Boolean = true,
    val isLoading: Boolean = false,
    val balances: List<WalletBalance> = emptyList(),
    val exchangeRate: ExchangeRate? = null,
    val recentTransactions: List<TransactionSummary> = emptyList(),
    val isBottomSheetVisible: Boolean = false,
    val userInitials: String = "US",
    val userName: String = "User",
    val unreadNotificationsCount: Int = 3,
    val errorMessage: String? = null
) : UiState

sealed interface WalletIntent : ViewIntent {
    data object LoadWalletData : WalletIntent
    data object ToggleBalanceVisibility : WalletIntent
    data class SelectCurrency(val currency: Currency) : WalletIntent
    data class SetBottomSheetVisible(val visible: Boolean) : WalletIntent
    data object RefreshData : WalletIntent
    data object AddMoneyClicked : WalletIntent
    data object SendMoneyClicked : WalletIntent
    data object TransactionsClicked : WalletIntent
    data object RecipientsClicked : WalletIntent
    data object GetUsAccountClicked : WalletIntent
    data object ProfileClicked : WalletIntent
    data object NotificationsClicked : WalletIntent
}

sealed interface WalletEffect : ViewEffect {
    data object NavigateToAddMoney : WalletEffect
    data object NavigateToSendMoney : WalletEffect
    data object NavigateToActivity : WalletEffect
    data object NavigateToRecipients : WalletEffect
    data object NavigateToGetUsAccount : WalletEffect
    data object NavigateToProfile : WalletEffect
    data class ShowMessage(val message: String) : WalletEffect
}

class WalletViewModel(
    private val walletRepository: WalletRepository
) : BaseViewModel<WalletIntent, WalletUiState, WalletEffect>(WalletUiState()) {

    init {
        sendIntent(WalletIntent.LoadWalletData)
    }

    override fun handleIntent(intent: WalletIntent) {
        when (intent) {
            WalletIntent.LoadWalletData -> loadData()
            WalletIntent.ToggleBalanceVisibility -> setState { copy(isBalanceVisible = !isBalanceVisible) }
            is WalletIntent.SelectCurrency -> selectCurrency(intent.currency)
            is WalletIntent.SetBottomSheetVisible -> setState { copy(isBottomSheetVisible = intent.visible) }
            WalletIntent.RefreshData -> loadData()
            WalletIntent.AddMoneyClicked -> setEffect { WalletEffect.NavigateToAddMoney }
            WalletIntent.SendMoneyClicked -> setEffect { WalletEffect.NavigateToSendMoney }
            WalletIntent.TransactionsClicked -> setEffect { WalletEffect.NavigateToActivity }
            WalletIntent.RecipientsClicked -> setEffect { WalletEffect.NavigateToRecipients }
            WalletIntent.GetUsAccountClicked -> setEffect { WalletEffect.NavigateToGetUsAccount }
            WalletIntent.ProfileClicked -> setEffect { WalletEffect.NavigateToProfile }
            WalletIntent.NotificationsClicked -> setEffect { WalletEffect.ShowMessage("Notifications clicked") }
        }
    }

    private fun loadData() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            walletRepository.getBalances().collect { result ->
                result.onSuccess { balances ->
                    setState { copy(balances = balances, isLoading = false) }
                    loadExchangeRate(currentState.selectedCurrency)
                }
            }
        }
        viewModelScope.launch {
            walletRepository.getRecentTransactions().collect { result ->
                result.onSuccess { transactions ->
                    setState { copy(recentTransactions = transactions) }
                }
            }
        }
    }

    private fun selectCurrency(currency: Currency) {
        setState { copy(selectedCurrency = currency, isBottomSheetVisible = false) }
        loadExchangeRate(currency)
    }

    private fun loadExchangeRate(currency: Currency) {
        val targetCurrency = if (currency == Currency.NGN) Currency.USD else Currency.NGN
        viewModelScope.launch {
            walletRepository.getExchangeRate(currency, targetCurrency).collect { result ->
                result.onSuccess { rate ->
                    setState { copy(exchangeRate = rate) }
                }
            }
        }
    }
}
