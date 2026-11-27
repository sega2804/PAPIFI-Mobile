package com.papifi.activity.domain

import com.papifi.model.Money
import com.papifi.model.TransactionStatus
import kotlinx.serialization.Serializable

@Serializable
data class TransactionActivityItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Money,
    val status: TransactionStatus,
    val type: String, // SEND, RECEIVE, CONVERT, ADD
    val date: String,
    val reference: String,
    val fee: Money?,
    val exchangeRate: Double?
)
