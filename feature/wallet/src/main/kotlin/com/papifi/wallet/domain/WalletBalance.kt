package com.papifi.wallet.domain

import com.papifi.model.Currency
import com.papifi.model.Money

data class WalletBalance(
    val currency: Currency,
    val available: Money,
    val total: Money,
    val reserved: Money,
    val isProvisioned: Boolean,
    val asOf: String
)
