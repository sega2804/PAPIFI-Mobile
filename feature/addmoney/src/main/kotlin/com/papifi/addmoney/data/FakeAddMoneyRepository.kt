package com.papifi.addmoney.data

import com.papifi.addmoney.domain.AddMoneyRepository
import com.papifi.addmoney.domain.FundingDetails
import com.papifi.addmoney.domain.FundingMethod
import com.papifi.common.Result
import com.papifi.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAddMoneyRepository : AddMoneyRepository {

    override fun getFundingMethods(currency: Currency): Flow<Result<List<FundingMethod>>> = flow {
        emit(Result.Loading)
        val methods = when (currency) {
            Currency.NGN -> listOf(
                FundingMethod.VIRTUAL_ACCOUNT,
                FundingMethod.BANK_TRANSFER,
                FundingMethod.CARD
            )
            Currency.USD, Currency.GBP, Currency.EUR -> listOf(
                FundingMethod.FOREIGN_WIRE,
                FundingMethod.CARD
            )
        }
        emit(Result.Success(methods))
    }

    override fun getFundingDetails(
        currency: Currency,
        method: FundingMethod
    ): Flow<Result<FundingDetails>> = flow {
        emit(Result.Loading)
        val details = when (currency) {
            Currency.NGN -> FundingDetails(
                currency = Currency.NGN,
                bankName = "Wema Bank",
                accountNumber = "9918237461",
                accountName = "PAPIFI / User",
                instructions = "Transfers to this dedicated virtual account reflect instantly in your PAPIFI NGN wallet",
                isAvailable = true
            )
            Currency.USD -> FundingDetails(
                currency = Currency.USD,
                bankName = "Bank of America",
                accountNumber = "88492018472",
                accountName = "PAPIFI Global LLC",
                routingNumber = "121000358",
                swiftCode = "BOFAUS3NXXX",
                bankAddress = "100 N Tryon St, Charlotte, NC 28255, USA",
                referenceCode = "PAP-USD-991823",
                instructions = "Wire transfers usually settle in 1-2 business days. Include reference code in transfer description.",
                isAvailable = true
            )
            Currency.GBP -> FundingDetails(
                currency = Currency.GBP,
                bankName = "Barclays Bank UK",
                accountNumber = "74829104",
                accountName = "PAPIFI UK Ltd",
                sortCode = "20-04-15",
                swiftCode = "BARCGB22",
                bankAddress = "1 Churchill Place, London, E14 5HP, UK",
                referenceCode = "PAP-GBP-991823",
                instructions = "UK Faster Payments reflect within minutes. Include reference code in transfer note.",
                isAvailable = true
            )
            Currency.EUR -> FundingDetails(
                currency = Currency.EUR,
                bankName = "Deutsche Bank AG",
                accountNumber = "DE89370400440532013000",
                accountName = "PAPIFI Europe GmbH",
                iban = "DE89370400440532013000",
                swiftCode = "DBETDEFFXXX",
                bankAddress = "Taunusanlage 12, 60325 Frankfurt, Germany",
                referenceCode = "PAP-EUR-991823",
                instructions = "SEPA transfers take 1 business day. Include reference code in payment purpose.",
                isAvailable = true
            )
        }
        emit(Result.Success(details))
    }
}
