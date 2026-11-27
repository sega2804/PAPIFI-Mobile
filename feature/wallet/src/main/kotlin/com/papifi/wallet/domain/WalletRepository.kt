package com.papifi.wallet.domain

import com.papifi.common.Result
import com.papifi.model.Currency
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getBalances(): Flow<Result<List<WalletBalance>>>
    fun getExchangeRate(from: Currency, to: Currency): Flow<Result<ExchangeRate>>
    fun getRecentTransactions(): Flow<Result<List<TransactionSummary>>>
}
