package com.papifi.activity.domain

import com.papifi.common.Result
import com.papifi.model.Currency
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getTransactions(filterType: String?, currency: Currency?): Flow<Result<List<TransactionActivityItem>>>
    fun getTransactionDetail(id: String): Flow<Result<TransactionActivityItem>>
}
