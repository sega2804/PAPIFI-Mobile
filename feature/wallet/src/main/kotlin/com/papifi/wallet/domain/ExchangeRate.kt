package com.papifi.wallet.domain

import com.papifi.model.Currency

data class ExchangeRate(
    val fromCurrency: Currency,
    val toCurrency: Currency,
    val rate: Double,
    val updatedAt: String
)
