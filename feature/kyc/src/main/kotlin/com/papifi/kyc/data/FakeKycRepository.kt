package com.papifi.kyc.data

import com.papifi.common.Result
import com.papifi.kyc.domain.KycDocument
import com.papifi.kyc.domain.KycRepository
import com.papifi.model.KycStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeKycRepository : KycRepository {

    private val kycStatusState = MutableStateFlow(KycStatus.NOT_STARTED)
    private var currentDocument: KycDocument? = null

    override fun getKycStatus(): Flow<Result<KycStatus>> {
        return kycStatusState.asStateFlow().map { Result.Success(it) }
    }

    override suspend fun saveDocument(document: KycDocument): Result<Unit> {
        currentDocument = document
        return Result.Success(Unit)
    }

    override suspend fun getCurrentDocument(): Result<KycDocument?> {
        return Result.Success(currentDocument)
    }

    override suspend fun submitVerification(selfieUri: String?): Result<KycStatus> {
        kycStatusState.value = KycStatus.PENDING
        return Result.Success(KycStatus.PENDING)
    }

    override suspend fun resetVerification(): Result<Unit> {
        kycStatusState.value = KycStatus.NOT_STARTED
        currentDocument = null
        return Result.Success(Unit)
    }
}
