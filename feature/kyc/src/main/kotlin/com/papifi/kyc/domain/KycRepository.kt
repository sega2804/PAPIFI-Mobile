package com.papifi.kyc.domain

import com.papifi.common.Result
import com.papifi.model.KycStatus
import kotlinx.coroutines.flow.Flow

interface KycRepository {
    fun getKycStatus(): Flow<Result<KycStatus>>
    suspend fun saveDocument(document: KycDocument): Result<Unit>
    suspend fun getCurrentDocument(): Result<KycDocument?>
    suspend fun submitVerification(selfieUri: String?): Result<KycStatus>
    suspend fun resetVerification(): Result<Unit>
}
