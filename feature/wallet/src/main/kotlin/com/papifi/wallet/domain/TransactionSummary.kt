package com.papifi.wallet.domain

import com.papifi.model.Money
import com.papifi.model.TransactionStatus

data class TransactionSummary(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Money,
    val status: TransactionStatus,
    val date: String,
    val isCredit: Boolean
)
