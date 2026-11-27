package com.papifi.receive.di

import com.papifi.receive.data.FakeReceiveRepository
import com.papifi.receive.domain.ReceiveRepository
import com.papifi.receive.ui.ReceiveViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val receiveModule = module {
    single<ReceiveRepository> { FakeReceiveRepository() }
    viewModel { ReceiveViewModel(get()) }
}
