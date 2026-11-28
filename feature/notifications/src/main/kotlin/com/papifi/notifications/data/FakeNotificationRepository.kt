package com.papifi.notifications.data

import com.papifi.common.Result
import com.papifi.notifications.domain.AppNotification
import com.papifi.notifications.domain.NotificationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeNotificationRepository : NotificationRepository {
    private val notificationsFlow = MutableStateFlow<List<AppNotification>>(
        listOf(
            AppNotification(
                id = "notif_1",
                title = "Money Received",
                body = "You received $150.00 from Sarah Connor via Wire Transfer.",
                timestamp = "10 mins ago",
                isRead = false,
                type = "TRANSACTION"
            ),
            AppNotification(
                id = "notif_2",
                title = "KYC Verification Successful",
                body = "Your Tier 2 identity verification has been approved.",
                timestamp = "2 hours ago",
                isRead = false,
                type = "KYC"
            ),
            AppNotification(
                id = "notif_3",
                title = "Currency Exchange Completed",
                body = "Successfully converted $5,000.00 to NGN.",
                timestamp = "1 day ago",
                isRead = true,
                type = "EXCHANGE"
            ),
            AppNotification(
                id = "notif_4",
                title = "Security Alert",
                body = "New login detected from Android device in Lagos, NG.",
                timestamp = "3 days ago",
                isRead = true,
                type = "SECURITY"
            )
        )
    )

    override fun getNotifications(): Flow<Result<List<AppNotification>>> = flow {
        emit(Result.Loading)
        delay(150)
        emit(Result.Success(notificationsFlow.value))
    }

    override fun markAsRead(id: String): Flow<Result<Unit>> = flow {
        val current = notificationsFlow.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        notificationsFlow.value = current
        emit(Result.Success(Unit))
    }

    override fun markAllAsRead(): Flow<Result<Unit>> = flow {
        val current = notificationsFlow.value.map { it.copy(isRead = true) }
        notificationsFlow.value = current
        emit(Result.Success(Unit))
    }
}
