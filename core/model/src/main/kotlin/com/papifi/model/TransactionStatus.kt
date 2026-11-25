package com.papifi.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionStatus {
    PENDING,
    PROCESSING,
    SUCCESSFUL,
    FAILED,
    UNKNOWN
}
