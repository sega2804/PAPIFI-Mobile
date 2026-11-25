package com.papifi.ui

import com.papifi.model.Money
import java.text.NumberFormat
import java.util.Currency as JavaCurrency
import java.util.Locale

object MoneyFormatter {
    fun format(money: Money, locale: Locale = Locale.getDefault()): String {
        val format = NumberFormat.getCurrencyInstance(locale).apply {
            currency = JavaCurrency.getInstance(money.currency.code)
        }
        return format.format(money.majorUnits)
    }
}
