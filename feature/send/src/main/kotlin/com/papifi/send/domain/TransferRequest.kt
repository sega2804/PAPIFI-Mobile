package com.papifi.send.domain

import com.papifi.model.Money
import kotlinx.serialization.Serializable

@Serializable
data class TransferRequest(
    val recipientId: String,
    val route: TransferRoute,
    val amount: Money,
    val note: String,
    val idempotencyKey: String
)
