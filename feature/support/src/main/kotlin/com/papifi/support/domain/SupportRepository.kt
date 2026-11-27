package com.papifi.support.domain

import com.papifi.common.Result
import kotlinx.coroutines.flow.Flow

interface SupportRepository {
    fun submitTicket(ticket: SupportTicket): Flow<Result<Unit>>
}
