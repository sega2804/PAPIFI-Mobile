package com.papifi.send.domain

import com.papifi.model.TransactionStatus
import kotlinx.serialization.Serializable

@Serializable
data class TransferResult(
    val transactionReference: String,
    val status: TransactionStatus,
    val message: String,
    val timestamp: String
)
