package com.papifi.model

import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
data class Money(
    val minorUnits: Long,
    val currency: Currency
) {
    val majorUnits: Double
        get() = minorUnits.toDouble() / 10.0.pow(currency.decimals.toDouble())
}
