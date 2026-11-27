package com.papifi.exchange.ui.quote

import androidx.lifecycle.viewModelScope
import com.papifi.common.onError
import com.papifi.common.onSuccess
import com.papifi.exchange.domain.ExchangeRepository
import com.papifi.exchange.domain.ExecutableQuote
import com.papifi.model.Currency
import com.papifi.model.Money
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ExecutableQuoteUiState(
    val quote: ExecutableQuote? = null,
    val remainingSeconds: Int = 60,
    val isExpired: Boolean = false,
    val isLoading: Boolean = false,
    val isExecuting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface ExecutableQuoteIntent : ViewIntent {
    data class InitQuote(
        val fromCurrency: Currency,
        val toCurrency: Currency,
        val fromAmountMinor: Long
    ) : ExecutableQuoteIntent
    data object ConfirmExchangeClicked : ExecutableQuoteIntent
    data object RefreshQuoteClicked : ExecutableQuoteIntent
    data object DismissSuccessDialog : ExecutableQuoteIntent
}

sealed interface ExecutableQuoteEffect : ViewEffect {
    data object NavigateToHome : ExecutableQuoteEffect
    data class ShowToast(val message: String) : ExecutableQuoteEffect
}

class ExecutableQuoteViewModel(
    private val exchangeRepository: ExchangeRepository
) : BaseViewModel<ExecutableQuoteIntent, ExecutableQuoteUiState, ExecutableQuoteEffect>(
    ExecutableQuoteUiState()
) {

    private var timerJob: Job? = null
    private var lastFromCurrency: Currency? = null
    private var lastToCurrency: Currency? = null
    private var lastFromAmountMinor: Long? = null

    override fun handleIntent(intent: ExecutableQuoteIntent) {
        when (intent) {
            is ExecutableQuoteIntent.InitQuote -> {
                lastFromCurrency = intent.fromCurrency
                lastToCurrency = intent.toCurrency
                lastFromAmountMinor = intent.fromAmountMinor
                loadQuote(intent.fromCurrency, intent.toCurrency, intent.fromAmountMinor)
            }
            ExecutableQuoteIntent.ConfirmExchangeClicked -> confirmExchange()
            ExecutableQuoteIntent.RefreshQuoteClicked -> refreshQuote()
            ExecutableQuoteIntent.DismissSuccessDialog -> setEffect { ExecutableQuoteEffect.NavigateToHome }
        }
    }

    private fun loadQuote(fromCurrency: Currency, toCurrency: Currency, fromAmountMinor: Long) {
        setState { copy(isLoading = true, errorMessage = null, isExpired = false, isSuccess = false) }
        viewModelScope.launch {
            val fromMoney = Money(fromAmountMinor, fromCurrency)
            exchangeRepository.getExecutableQuote(fromMoney, toCurrency)
                .collect { result ->
                    result.onSuccess { quote ->
                        val initialSeconds = ((quote.expiresAtEpochMs - System.currentTimeMillis()) / 1000L).toInt().coerceAtLeast(0)
                        setState {
                            copy(
                                quote = quote,
                                remainingSeconds = initialSeconds,
                                isExpired = initialSeconds <= 0,
                                isLoading = false
                            )
                        }
                        startCountdownTimer()
                    }.onError { _, message ->
                        setState { copy(isLoading = false, errorMessage = message ?: "Failed to fetch quote.") }
                    }
                }
        }
    }

    private fun startCountdownTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val quote = currentState.quote ?: break
                val remaining = ((quote.expiresAtEpochMs - System.currentTimeMillis()) / 1000L).toInt()
                if (remaining <= 0) {
                    setState { copy(remainingSeconds = 0, isExpired = true) }
                    break
                } else {
                    setState { copy(remainingSeconds = remaining) }
                }
            }
        }
    }

    private fun confirmExchange() {
        val quote = currentState.quote ?: return
        if (currentState.isExpired) {
            setState { copy(errorMessage = "Quote has expired. Please refresh the quote.") }
            return
        }

        setState { copy(isExecuting = true, errorMessage = null) }
        viewModelScope.launch {
            exchangeRepository.executeExchange(quote.quoteId)
                .collect { result ->
                    result.onSuccess {
                        timerJob?.cancel()
                        setState { copy(isExecuting = false, isSuccess = true) }
                    }.onError { _, message ->
                        setState { copy(isExecuting = false, errorMessage = message ?: "Exchange failed.") }
                    }
                }
        }
    }

    private fun refreshQuote() {
        val from = lastFromCurrency ?: currentState.quote?.fromAmount?.currency ?: return
        val to = lastToCurrency ?: currentState.quote?.toAmount?.currency ?: return
        val minor = lastFromAmountMinor ?: currentState.quote?.fromAmount?.minorUnits ?: return
        loadQuote(from, to, minor)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
