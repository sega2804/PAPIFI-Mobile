package com.papifi.wallet.di

import com.papifi.wallet.data.FakeWalletRepository
import com.papifi.wallet.domain.WalletRepository
import com.papifi.wallet.ui.WalletViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val walletModule = module {
    single<WalletRepository> { FakeWalletRepository() }
    viewModel { WalletViewModel(get()) }
}
