package com.papifi.activity.di

import com.papifi.activity.data.FakeActivityRepository
import com.papifi.activity.domain.ActivityRepository
import com.papifi.activity.ui.list.ActivityViewModel
import com.papifi.activity.ui.receipt.TransactionReceiptViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityModule = module {
    single<ActivityRepository> { FakeActivityRepository() }
    viewModel { ActivityViewModel(get()) }
    viewModel { TransactionReceiptViewModel(get()) }
}
