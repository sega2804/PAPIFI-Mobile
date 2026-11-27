package com.papifi.send.data

import com.papifi.common.Result
import com.papifi.model.Currency
import com.papifi.model.TransactionStatus
import com.papifi.send.domain.Beneficiary
import com.papifi.send.domain.SendRepository
import com.papifi.send.domain.TransferRequest
import com.papifi.send.domain.TransferResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FakeSendRepository : SendRepository {

    private val initialBeneficiaries = mutableListOf(
        Beneficiary("1", "Amina Bello", "0123456789", "Access Bank", Currency.NGN, null),
        Beneficiary("2", "John Doe", "9876543210", "Bank of America", Currency.USD, null),
        Beneficiary("3", "Chidi Okafor", "1122334455", "Guaranty Trust Bank", Currency.NGN, null),
        Beneficiary("4", "Sarah Jenkins", "5544332211", "Barclays Bank", Currency.GBP, null)
    )

    private val beneficiariesFlow = MutableStateFlow<List<Beneficiary>>(initialBeneficiaries)

    override fun getBeneficiaries(): Flow<Result<List<Beneficiary>>> = flow {
        emit(Result.Loading)
        emit(Result.Success(beneficiariesFlow.value))
    }

    override fun addBeneficiary(beneficiary: Beneficiary): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        delay(300)
        val current = beneficiariesFlow.value.toMutableList()
        current.add(0, beneficiary)
        beneficiariesFlow.value = current
        emit(Result.Success(Unit))
    }

    override fun executeTransfer(request: TransferRequest, pin: String): Flow<Result<TransferResult>> = flow {
        emit(Result.Loading)
        delay(1000)
        if (pin.length != 4) {
            emit(Result.Error(IllegalArgumentException("Invalid 4-digit PIN")))
            return@flow
        }
        val ref = "PAP-TX-${System.currentTimeMillis().toString().takeLast(6)}"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        val result = TransferResult(
            transactionReference = ref,
            status = TransactionStatus.SUCCESSFUL,
            message = "Transfer completed successfully",
            timestamp = timestamp
        )
        emit(Result.Success(result))
    }
}
