package com.papifi.addmoney.domain

import com.papifi.model.Currency

data class FundingDetails(
    val currency: Currency,
    val bankName: String,
    val accountNumber: String,
    val accountName: String,
    val routingNumber: String? = null,
    val swiftCode: String? = null,
    val iban: String? = null,
    val referenceCode: String? = null,
    val instructions: String,
    val isAvailable: Boolean = true,
    val sortCode: String? = null,
    val bankAddress: String? = null
)
