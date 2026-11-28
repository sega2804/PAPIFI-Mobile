package com.papifi.notifications.domain

import com.papifi.common.Result
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(): Flow<Result<List<AppNotification>>>
    fun markAsRead(id: String): Flow<Result<Unit>>
    fun markAllAsRead(): Flow<Result<Unit>>
}
