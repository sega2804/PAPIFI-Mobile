package com.papifi.activity.data

import com.papifi.activity.domain.ActivityRepository
import com.papifi.activity.domain.TransactionActivityItem
import com.papifi.common.Result
import com.papifi.model.Currency
import com.papifi.model.Money
import com.papifi.model.TransactionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeActivityRepository : ActivityRepository {

    private val transactions = listOf(
        TransactionActivityItem(
            id = "tx_001",
            title = "Sent to John Doe",
            subtitle = "Bank Transfer • Access Bank",
            amount = Money(2500000L, Currency.NGN), // ₦25,000.00
            status = TransactionStatus.SUCCESSFUL,
            type = "SEND",
            date = "Oct 24, 2023 • 14:32",
            reference = "PAP-NGN-893241",
            fee = Money(5000L, Currency.NGN),
            exchangeRate = null
        ),
        TransactionActivityItem(
            id = "tx_002",
            title = "Received from Sarah Connor",
            subtitle = "Wire Transfer",
            amount = Money(15000L, Currency.USD), // $150.00
            status = TransactionStatus.SUCCESSFUL,
            type = "RECEIVE",
            date = "Oct 23, 2023 • 09:15",
            reference = "PAP-USD-441290",
            fee = Money(0L, Currency.USD),
            exchangeRate = null
        ),
        TransactionActivityItem(
            id = "tx_003",
            title = "Converted USD to NGN",
            subtitle = "Currency Exchange",
            amount = Money(500000L, Currency.USD), // $5,000.00 converted
            status = TransactionStatus.SUCCESSFUL,
            type = "CONVERT",
            date = "Oct 22, 2023 • 16:45",
            reference = "PAP-CON-772134",
            fee = Money(200L, Currency.USD),
            exchangeRate = 1450.0
        ),
        TransactionActivityItem(
            id = "tx_004",
            title = "Added Money via Card",
            subtitle = "Debit Card • •••• 4242",
            amount = Money(5000000L, Currency.NGN), // ₦50,000.00
            status = TransactionStatus.SUCCESSFUL,
            type = "ADD",
            date = "Oct 20, 2023 • 11:00",
            reference = "PAP-ADD-119283",
            fee = Money(15000L, Currency.NGN),
            exchangeRate = null
        ),
        TransactionActivityItem(
            id = "tx_005",
            title = "Sent to Apex Global",
            subtitle = "GBP Transfer",
            amount = Money(35000L, Currency.GBP), // £350.00
            status = TransactionStatus.PENDING,
            type = "SEND",
            date = "Oct 25, 2023 • 08:00",
            reference = "PAP-GBP-992381",
            fee = Money(100L, Currency.GBP),
            exchangeRate = null
        ),
        TransactionActivityItem(
            id = "tx_006",
            title = "Received EUR Deposit",
            subtitle = "SEPA Transfer",
            amount = Money(120000L, Currency.EUR), // €1,200.00
            status = TransactionStatus.FAILED,
            type = "RECEIVE",
            date = "Oct 19, 2023 • 18:20",
            reference = "PAP-EUR-554123",
            fee = Money(0L, Currency.EUR),
            exchangeRate = null
        )
    )

    override fun getTransactions(filterType: String?, currency: Currency?): Flow<Result<List<TransactionActivityItem>>> = flow {
        emit(Result.Loading)
        delay(300)
        var filtered = transactions
        if (!filterType.isNullOrBlank() && filterType != "ALL") {
            filtered = filtered.filter { it.type.equals(filterType, ignoreCase = true) }
        }
        if (currency != null) {
            filtered = filtered.filter { it.amount.currency == currency }
        }
        emit(Result.Success(filtered))
    }

    override fun getTransactionDetail(id: String): Flow<Result<TransactionActivityItem>> = flow {
        emit(Result.Loading)
        delay(200)
        val tx = transactions.find { it.id == id }
        if (tx != null) {
            emit(Result.Success(tx))
        } else {
            emit(Result.Error(IllegalArgumentException("Transaction not found"), "Transaction not found"))
        }
    }
}
