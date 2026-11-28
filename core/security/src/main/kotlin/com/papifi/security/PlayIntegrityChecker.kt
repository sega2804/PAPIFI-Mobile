package com.papifi.security

import com.papifi.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface PlayIntegrityChecker {
    fun checkIntegrity(): Flow<Result<Boolean>>
}

class MockPlayIntegrityChecker : PlayIntegrityChecker {
    override fun checkIntegrity(): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        emit(Result.Success(true))
    }
}
