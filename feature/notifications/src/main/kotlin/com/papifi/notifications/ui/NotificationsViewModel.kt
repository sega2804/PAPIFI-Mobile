package com.papifi.notifications.ui

import androidx.lifecycle.viewModelScope
import com.papifi.common.Result
import com.papifi.notifications.domain.AppNotification
import com.papifi.notifications.domain.NotificationRepository
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val notifications: List<AppNotification> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState {
    val unreadCount: Int get() = notifications.count { !it.isRead }
}

sealed interface NotificationsIntent : ViewIntent {
    data object LoadNotifications : NotificationsIntent
    data class MarkAsRead(val id: String) : NotificationsIntent
    data object MarkAllAsRead : NotificationsIntent
}

sealed interface NotificationsEffect : ViewEffect {
    data class ShowMessage(val message: String) : NotificationsEffect
}

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository
) : BaseViewModel<NotificationsIntent, NotificationsUiState, NotificationsEffect>(NotificationsUiState()) {

    init {
        sendIntent(NotificationsIntent.LoadNotifications)
    }

    override fun handleIntent(intent: NotificationsIntent) {
        when (intent) {
            NotificationsIntent.LoadNotifications -> loadNotifications()
            is NotificationsIntent.MarkAsRead -> markAsRead(intent.id)
            NotificationsIntent.MarkAllAsRead -> markAllAsRead()
        }
    }

    private fun loadNotifications() {
        setState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            notificationRepository.getNotifications().collectLatest { result ->
                when (result) {
                    is Result.Success -> setState { copy(notifications = result.data, isLoading = false) }
                    is Result.Error -> setState { copy(isLoading = false, errorMessage = result.message) }
                    is Result.Loading -> setState { copy(isLoading = true) }
                }
            }
        }
    }

    private fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id).collectLatest { result ->
                if (result is Result.Success) {
                    loadNotifications()
                }
            }
        }
    }

    private fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead().collectLatest { result ->
                if (result is Result.Success) {
                    loadNotifications()
                    setEffect { NotificationsEffect.ShowMessage("All notifications marked as read") }
                }
            }
        }
    }
}
