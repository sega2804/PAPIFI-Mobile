package com.papifi.addmoney.di

import com.papifi.addmoney.data.FakeAddMoneyRepository
import com.papifi.addmoney.domain.AddMoneyRepository
import com.papifi.addmoney.ui.bankdetails.NgnBankDetailsViewModel
import com.papifi.addmoney.ui.foreign.ForeignDepositDetailsViewModel
import com.papifi.addmoney.ui.pending.FundingPendingViewModel
import com.papifi.addmoney.ui.selector.AddMoneySelectorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val addMoneyModule = module {
    single<AddMoneyRepository> { FakeAddMoneyRepository() }
    viewModel { AddMoneySelectorViewModel(get()) }
    viewModel { NgnBankDetailsViewModel(get()) }
    viewModel { ForeignDepositDetailsViewModel(get()) }
    viewModel { FundingPendingViewModel() }
}
