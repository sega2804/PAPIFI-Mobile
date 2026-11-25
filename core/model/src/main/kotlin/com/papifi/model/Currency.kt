package com.papifi.model

import kotlinx.serialization.Serializable

@Serializable
enum class Currency(val code: String, val symbol: String, val decimals: Int) {
    USD("USD", "$", 2),
    GBP("GBP", "£", 2),
    EUR("EUR", "€", 2),
    NGN("NGN", "₦", 2);
}
