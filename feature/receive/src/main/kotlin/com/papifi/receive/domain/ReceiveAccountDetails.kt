package com.papifi.receive.domain

import com.papifi.model.Currency

data class ReceiveAccountDetails(
    val currency: Currency,
    val accountName: String,
    val accountNumber: String,
    val bankName: String,
    val routingNumber: String?,
    val swiftCode: String?,
    val iban: String?,
    val isProvisioned: Boolean
)
