package com.papifi.exchange.data

import com.papifi.common.Result
import com.papifi.exchange.domain.ExchangeRepository
import com.papifi.exchange.domain.ExecutableQuote
import com.papifi.exchange.domain.IndicativeRate
import com.papifi.model.Currency
import com.papifi.model.Money
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.pow

class FakeExchangeRepository : ExchangeRepository {

    private fun getRateToUsd(currency: Currency): Double = when (currency) {
        Currency.USD -> 1.0
        Currency.EUR -> 0.92
        Currency.GBP -> 0.78
        Currency.NGN -> 1550.0
    }

    private fun calculateRate(from: Currency, to: Currency): Double {
        if (from == to) return 1.0
        val fromUsd = getRateToUsd(from)
        val toUsd = getRateToUsd(to)
        return toUsd / fromUsd
    }

    override fun getIndicativeRate(from: Currency, to: Currency): Flow<Result<IndicativeRate>> = flow {
        val rate = calculateRate(from, to)
        val indicativeRate = IndicativeRate(
            fromCurrency = from,
            toCurrency = to,
            rate = rate,
            asOf = "Just now"
        )
        emit(Result.Success(indicativeRate))
    }

    override fun getExecutableQuote(
        fromAmount: Money,
        toCurrency: Currency
    ): Flow<Result<ExecutableQuote>> = flow {
        val rate = calculateRate(fromAmount.currency, toCurrency)
        val targetMajorUnits = fromAmount.majorUnits * rate
        val targetMinorUnits = (targetMajorUnits * 10.0.pow(toCurrency.decimals.toDouble())).toLong()
        val toAmount = Money(minorUnits = targetMinorUnits, currency = toCurrency)

        val feeMinor = (fromAmount.minorUnits * 0.005).toLong().coerceAtLeast(100L)
        val fee = Money(minorUnits = feeMinor, currency = fromAmount.currency)

        val quote = ExecutableQuote(
            quoteId = "QUOTE_${System.currentTimeMillis()}",
            fromAmount = fromAmount,
            toAmount = toAmount,
            guaranteedRate = rate,
            fee = fee,
            expiresAtEpochMs = System.currentTimeMillis() + 60_000L
        )
        emit(Result.Success(quote))
    }

    override fun executeExchange(quoteId: String): Flow<Result<Unit>> = flow {
        delay(500L)
        emit(Result.Success(Unit))
    }
}
