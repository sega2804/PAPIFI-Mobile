package com.papifi.wallet.data

import com.papifi.common.Result
import com.papifi.model.Currency
import com.papifi.model.Money
import com.papifi.model.TransactionStatus
import com.papifi.wallet.domain.ExchangeRate
import com.papifi.wallet.domain.TransactionSummary
import com.papifi.wallet.domain.WalletBalance
import com.papifi.wallet.domain.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWalletRepository : WalletRepository {

    override fun getBalances(): Flow<Result<List<WalletBalance>>> {
        val mockBalances = listOf(
            WalletBalance(
                currency = Currency.NGN,
                available = Money(minorUnits = 155000000L, currency = Currency.NGN),
                total = Money(minorUnits = 157500000L, currency = Currency.NGN),
                reserved = Money(minorUnits = 2500000L, currency = Currency.NGN),
                isProvisioned = true,
                asOf = "2025-02-23"
            ),
            WalletBalance(
                currency = Currency.USD,
                available = Money(minorUnits = 250000L, currency = Currency.USD),
                total = Money(minorUnits = 250000L, currency = Currency.USD),
                reserved = Money(minorUnits = 0L, currency = Currency.USD),
                isProvisioned = true,
                asOf = "2025-02-23"
            ),
            WalletBalance(
                currency = Currency.GBP,
                available = Money(minorUnits = 120000L, currency = Currency.GBP),
                total = Money(minorUnits = 125000L, currency = Currency.GBP),
                reserved = Money(minorUnits = 5000L, currency = Currency.GBP),
                isProvisioned = true,
                asOf = "2025-02-23"
            ),
            WalletBalance(
                currency = Currency.EUR,
                available = Money(minorUnits = 0L, currency = Currency.EUR),
                total = Money(minorUnits = 0L, currency = Currency.EUR),
                reserved = Money(minorUnits = 0L, currency = Currency.EUR),
                isProvisioned = false,
                asOf = "2025-02-23"
            )
        )
        return flowOf(Result.Success(mockBalances))
    }

    override fun getExchangeRate(from: Currency, to: Currency): Flow<Result<ExchangeRate>> {
        val rateValue = when {
            from == Currency.USD && to == Currency.NGN -> 1550.0
            from == Currency.NGN && to == Currency.USD -> 1.0 / 1550.0
            from == Currency.GBP && to == Currency.NGN -> 1950.0
            from == Currency.NGN && to == Currency.GBP -> 1.0 / 1950.0
            from == Currency.EUR && to == Currency.NGN -> 1680.0
            from == Currency.NGN && to == Currency.EUR -> 1.0 / 1680.0
            from == Currency.USD && to == Currency.EUR -> 0.92
            from == Currency.EUR && to == Currency.USD -> 1.08
            from == Currency.GBP && to == Currency.USD -> 1.26
            from == Currency.USD && to == Currency.GBP -> 0.79
            else -> 1.0
        }
        val rate = ExchangeRate(
            fromCurrency = from,
            toCurrency = to,
            rate = rateValue,
            updatedAt = "Just now"
        )
        return flowOf(Result.Success(rate))
    }

    override fun getRecentTransactions(): Flow<Result<List<TransactionSummary>>> {
        val transactions = listOf(
            TransactionSummary(
                id = "tx_001",
                title = "Salary Deposit",
                subtitle = "Remote Work Corp",
                amount = Money(minorUnits = 85000000L, currency = Currency.NGN),
                status = TransactionStatus.SUCCESSFUL,
                date = "Today, 2:30 PM",
                isCredit = true
            ),
            TransactionSummary(
                id = "tx_002",
                title = "Netflix Subscription",
                subtitle = "Entertainment",
                amount = Money(minorUnits = 1599L, currency = Currency.USD),
                status = TransactionStatus.SUCCESSFUL,
                date = "Yesterday, 10:15 AM",
                isCredit = false
            ),
            TransactionSummary(
                id = "tx_003",
                title = "Transfer to Chime",
                subtitle = "Bank Transfer",
                amount = Money(minorUnits = 150000L, currency = Currency.USD),
                status = TransactionStatus.PENDING,
                date = "Feb 21, 2025",
                isCredit = false
            ),
            TransactionSummary(
                id = "tx_004",
                title = "Online Shopping",
                subtitle = "Amazon US",
                amount = Money(minorUnits = 8999L, currency = Currency.USD),
                status = TransactionStatus.FAILED,
                date = "Feb 20, 2025",
                isCredit = false
            ),
            TransactionSummary(
                id = "tx_005",
                title = "Card Funding",
                subtitle = "Card Topup",
                amount = Money(minorUnits = 5000000L, currency = Currency.NGN),
                status = TransactionStatus.SUCCESSFUL,
                date = "Feb 18, 2025",
                isCredit = true
            )
        )
        return flowOf(Result.Success(transactions))
    }
}
