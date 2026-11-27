package com.papifi.support.domain

import kotlinx.serialization.Serializable

@Serializable
data class SupportTicket(
    val category: String,
    val subject: String,
    val description: String,
    val transactionReference: String?
)
