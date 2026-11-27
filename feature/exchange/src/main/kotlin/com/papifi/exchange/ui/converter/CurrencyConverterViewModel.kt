package com.papifi.exchange.ui.converter

import androidx.lifecycle.viewModelScope
import com.papifi.common.onSuccess
import com.papifi.exchange.domain.ExchangeRepository
import com.papifi.exchange.domain.IndicativeRate
import com.papifi.model.Currency
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.pow

data class CurrencyConverterUiState(
    val fromCurrency: Currency = Currency.USD,
    val toCurrency: Currency = Currency.NGN,
    val sourceAmountText: String = "100",
    val indicativeRate: IndicativeRate? = null,
    val calculatedTargetAmountText: String = "",
    val isLoadingRate: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface CurrencyConverterIntent : ViewIntent {
    data class SelectFromCurrency(val currency: Currency) : CurrencyConverterIntent
    data class SelectToCurrency(val currency: Currency) : CurrencyConverterIntent
    data object SwapCurrencies : CurrencyConverterIntent
    data class SourceAmountChanged(val amount: String) : CurrencyConverterIntent
    data object GetGuaranteedQuoteClicked : CurrencyConverterIntent
    data object LoadIndicativeRate : CurrencyConverterIntent
}

sealed interface CurrencyConverterEffect : ViewEffect {
    data class NavigateToExecutableQuote(
        val fromCurrency: Currency,
        val toCurrency: Currency,
        val fromAmountMinor: Long
    ) : CurrencyConverterEffect
    data class ShowError(val message: String) : CurrencyConverterEffect
}

class CurrencyConverterViewModel(
    private val exchangeRepository: ExchangeRepository
) : BaseViewModel<CurrencyConverterIntent, CurrencyConverterUiState, CurrencyConverterEffect>(
    CurrencyConverterUiState()
) {

    init {
        sendIntent(CurrencyConverterIntent.LoadIndicativeRate)
    }

    override fun handleIntent(intent: CurrencyConverterIntent) {
        when (intent) {
            is CurrencyConverterIntent.SelectFromCurrency -> {
                if (intent.currency == currentState.toCurrency) {
                    swapCurrencies()
                } else {
                    setState { copy(fromCurrency = intent.currency, errorMessage = null) }
                    loadIndicativeRate()
                }
            }
            is CurrencyConverterIntent.SelectToCurrency -> {
                if (intent.currency == currentState.fromCurrency) {
                    swapCurrencies()
                } else {
                    setState { copy(toCurrency = intent.currency, errorMessage = null) }
                    loadIndicativeRate()
                }
            }
            CurrencyConverterIntent.SwapCurrencies -> swapCurrencies()
            is CurrencyConverterIntent.SourceAmountChanged -> {
                val filtered = intent.amount.filter { it.isDigit() || it == '.' }
                setState { copy(sourceAmountText = filtered, errorMessage = null) }
                recalculateTargetAmount()
            }
            CurrencyConverterIntent.LoadIndicativeRate -> loadIndicativeRate()
            CurrencyConverterIntent.GetGuaranteedQuoteClicked -> getGuaranteedQuote()
        }
    }

    private fun swapCurrencies() {
        val newFrom = currentState.toCurrency
        val newTo = currentState.fromCurrency
        setState { copy(fromCurrency = newFrom, toCurrency = newTo, errorMessage = null) }
        loadIndicativeRate()
    }

    private fun loadIndicativeRate() {
        setState { copy(isLoadingRate = true) }
        viewModelScope.launch {
            exchangeRepository.getIndicativeRate(currentState.fromCurrency, currentState.toCurrency)
                .collect { result ->
                    result.onSuccess { rate ->
                        setState { copy(indicativeRate = rate, isLoadingRate = false) }
                        recalculateTargetAmount()
                    }
                }
        }
    }

    private fun recalculateTargetAmount() {
        val sourceValue = currentState.sourceAmountText.toDoubleOrNull() ?: 0.0
        val rate = currentState.indicativeRate?.rate ?: 0.0
        val targetValue = sourceValue * rate
        val formatted = if (targetValue > 0) {
            String.format(Locale.US, "%.2f", targetValue)
        } else {
            "0.00"
        }
        setState { copy(calculatedTargetAmountText = formatted) }
    }

    private fun getGuaranteedQuote() {
        val sourceValue = currentState.sourceAmountText.toDoubleOrNull()
        if (sourceValue == null || sourceValue <= 0) {
            setState { copy(errorMessage = "Please enter a valid source amount.") }
            setEffect { CurrencyConverterEffect.ShowError("Please enter a valid source amount.") }
            return
        }

        val minorUnits = (sourceValue * 10.0.pow(currentState.fromCurrency.decimals.toDouble())).toLong()

        setEffect {
            CurrencyConverterEffect.NavigateToExecutableQuote(
                fromCurrency = currentState.fromCurrency,
                toCurrency = currentState.toCurrency,
                fromAmountMinor = minorUnits
            )
        }
    }
}
