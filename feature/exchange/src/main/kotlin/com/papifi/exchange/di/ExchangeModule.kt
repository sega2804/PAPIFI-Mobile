package com.papifi.exchange.di

import com.papifi.exchange.data.FakeExchangeRepository
import com.papifi.exchange.domain.ExchangeRepository
import com.papifi.exchange.ui.converter.CurrencyConverterViewModel
import com.papifi.exchange.ui.quote.ExecutableQuoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val exchangeModule = module {
    single<ExchangeRepository> { FakeExchangeRepository() }
    viewModel { CurrencyConverterViewModel(get()) }
    viewModel { ExecutableQuoteViewModel(get()) }
}
