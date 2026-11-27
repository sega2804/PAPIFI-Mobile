package com.papifi.exchange.domain

import com.papifi.model.Currency

data class IndicativeRate(
    val fromCurrency: Currency,
    val toCurrency: Currency,
    val rate: Double,
    val asOf: String
)
