package com.papifi.exchange.domain

import com.papifi.common.Result
import com.papifi.model.Currency
import com.papifi.model.Money
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    fun getIndicativeRate(from: Currency, to: Currency): Flow<Result<IndicativeRate>>
    fun getExecutableQuote(fromAmount: Money, toCurrency: Currency): Flow<Result<ExecutableQuote>>
    fun executeExchange(quoteId: String): Flow<Result<Unit>>
}
