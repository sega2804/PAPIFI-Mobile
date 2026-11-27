package com.papifi.support.data

import com.papifi.common.Result
import com.papifi.support.domain.SupportRepository
import com.papifi.support.domain.SupportTicket
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSupportRepository : SupportRepository {
    override fun submitTicket(ticket: SupportTicket): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        delay(800)
        emit(Result.Success(Unit))
    }
}
