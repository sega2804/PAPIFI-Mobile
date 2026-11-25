package com.papifi.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatDate(timestampMillis: Long, pattern: String = "dd MMM yyyy, HH:mm", locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(Date(timestampMillis))
    }
}
