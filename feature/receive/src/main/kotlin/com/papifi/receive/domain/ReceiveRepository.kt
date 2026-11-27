package com.papifi.receive.domain

import com.papifi.common.Result
import com.papifi.model.Currency
import kotlinx.coroutines.flow.Flow

interface ReceiveRepository {
    fun getReceiveAccountDetails(currency: Currency): Flow<Result<ReceiveAccountDetails>>
}
