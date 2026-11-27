package com.papifi.support.di

import com.papifi.support.data.FakeSupportRepository
import com.papifi.support.domain.SupportRepository
import com.papifi.support.ui.SupportViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val supportModule = module {
    single<SupportRepository> { FakeSupportRepository() }
    viewModel { SupportViewModel(get()) }
}
