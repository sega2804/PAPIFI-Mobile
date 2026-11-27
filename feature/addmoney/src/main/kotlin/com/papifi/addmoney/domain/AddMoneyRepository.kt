package com.papifi.addmoney.domain

import com.papifi.common.Result
import com.papifi.model.Currency
import kotlinx.coroutines.flow.Flow

interface AddMoneyRepository {
    fun getFundingMethods(currency: Currency): Flow<Result<List<FundingMethod>>>
    fun getFundingDetails(currency: Currency, method: FundingMethod): Flow<Result<FundingDetails>>
}
