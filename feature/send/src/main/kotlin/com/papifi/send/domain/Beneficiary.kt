package com.papifi.send.domain

import com.papifi.model.Currency
import kotlinx.serialization.Serializable

@Serializable
data class Beneficiary(
    val id: String,
    val name: String,
    val accountNumber: String,
    val bankName: String,
    val currency: Currency,
    val avatarUrl: String? = null
)
