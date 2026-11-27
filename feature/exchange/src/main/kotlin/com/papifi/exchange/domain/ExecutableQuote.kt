package com.papifi.exchange.domain

import com.papifi.model.Money

data class ExecutableQuote(
    val quoteId: String,
    val fromAmount: Money,
    val toAmount: Money,
    val guaranteedRate: Double,
    val fee: Money,
    val expiresAtEpochMs: Long
)
