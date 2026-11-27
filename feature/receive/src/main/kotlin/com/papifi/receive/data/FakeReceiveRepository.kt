package com.papifi.receive.data

import com.papifi.common.Result
import com.papifi.model.Currency
import com.papifi.receive.domain.ReceiveAccountDetails
import com.papifi.receive.domain.ReceiveRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeReceiveRepository : ReceiveRepository {
    override fun getReceiveAccountDetails(currency: Currency): Flow<Result<ReceiveAccountDetails>> = flow {
        delay(300L)
        val details = when (currency) {
            Currency.NGN -> ReceiveAccountDetails(
                currency = Currency.NGN,
                accountName = "Papifi / John Doe",
                accountNumber = "0123456789",
                bankName = "Wema Bank / Papifi Pay",
                routingNumber = null,
                swiftCode = "WEMANGLA",
                iban = null,
                isProvisioned = true
            )
            Currency.USD -> ReceiveAccountDetails(
                currency = Currency.USD,
                accountName = "John Doe",
                accountNumber = "9876543210",
                bankName = "Evolve Bank & Trust",
                routingNumber = "122333444",
                swiftCode = "EVLVEUS33",
                iban = null,
                isProvisioned = true
            )
            Currency.GBP -> ReceiveAccountDetails(
                currency = Currency.GBP,
                accountName = "John Doe",
                accountNumber = "12345678",
                bankName = "Barclays Bank UK",
                routingNumber = "20-04-15",
                swiftCode = "BARCGB22",
                iban = "GB33BARC20041512345678",
                isProvisioned = false
            )
            Currency.EUR -> ReceiveAccountDetails(
                currency = Currency.EUR,
                accountName = "John Doe",
                accountNumber = "FR76300020000112345678901",
                bankName = "BNP Paribas France",
                routingNumber = null,
                swiftCode = "BNPAFRPP",
                iban = "FR76300020000112345678901",
                isProvisioned = false
            )
        }
        emit(Result.Success(details))
    }
}
