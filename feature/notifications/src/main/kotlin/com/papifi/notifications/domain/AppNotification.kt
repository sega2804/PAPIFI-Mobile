package com.papifi.notifications.domain

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val timestamp: String,
    val isRead: Boolean,
    val type: String
)
