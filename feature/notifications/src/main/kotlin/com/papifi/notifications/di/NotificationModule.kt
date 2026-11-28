package com.papifi.notifications.di

import com.papifi.notifications.data.FakeNotificationRepository
import com.papifi.notifications.domain.NotificationRepository
import com.papifi.notifications.ui.NotificationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val notificationModule = module {
    single<NotificationRepository> { FakeNotificationRepository() }
    viewModel { NotificationsViewModel(get()) }
}
