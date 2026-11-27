package com.papifi.send.domain

import com.papifi.common.Result
import kotlinx.coroutines.flow.Flow

interface SendRepository {
    fun getBeneficiaries(): Flow<Result<List<Beneficiary>>>
    fun addBeneficiary(beneficiary: Beneficiary): Flow<Result<Unit>>
    fun executeTransfer(request: TransferRequest, pin: String): Flow<Result<TransferResult>>
}
