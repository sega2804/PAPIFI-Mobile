package com.papifi.kyc.ui.document

import androidx.lifecycle.viewModelScope
import com.papifi.common.onSuccess
import com.papifi.kyc.domain.KycDocument
import com.papifi.kyc.domain.KycDocumentType
import com.papifi.kyc.domain.KycRepository
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class KycDocumentUploadUiState(
    val selectedDocumentType: KycDocumentType = KycDocumentType.NIN,
    val documentNumber: String = "",
    val frontImageUri: String? = null,
    val backImageUri: String? = null,
    val isFrontUploaded: Boolean = false,
    val isBackUploaded: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) : UiState

sealed interface KycDocumentUploadIntent : ViewIntent {
    data class SelectDocumentType(val documentType: KycDocumentType) : KycDocumentUploadIntent
    data class DocumentNumberChanged(val number: String) : KycDocumentUploadIntent
    data object FrontUploadClicked : KycDocumentUploadIntent
    data object BackUploadClicked : KycDocumentUploadIntent
    data object ContinueToSelfieClicked : KycDocumentUploadIntent
}

sealed interface KycDocumentUploadEffect : ViewEffect {
    data object NavigateToSelfie : KycDocumentUploadEffect
}

class KycDocumentUploadViewModel(
    private val kycRepository: KycRepository
) : BaseViewModel<KycDocumentUploadIntent, KycDocumentUploadUiState, KycDocumentUploadEffect>(
    KycDocumentUploadUiState()
) {

    override fun handleIntent(intent: KycDocumentUploadIntent) {
        when (intent) {
            is KycDocumentUploadIntent.SelectDocumentType -> {
                setState { copy(selectedDocumentType = intent.documentType, errorMessage = null) }
            }
            is KycDocumentUploadIntent.DocumentNumberChanged -> {
                setState { copy(documentNumber = intent.number, errorMessage = null) }
            }
            KycDocumentUploadIntent.FrontUploadClicked -> simulateFrontUpload()
            KycDocumentUploadIntent.BackUploadClicked -> simulateBackUpload()
            KycDocumentUploadIntent.ContinueToSelfieClicked -> submitDocument()
        }
    }

    private fun simulateFrontUpload() {
        setState {
            copy(
                frontImageUri = "simulated_front_id.png",
                isFrontUploaded = true,
                errorMessage = null
            )
        }
    }

    private fun simulateBackUpload() {
        setState {
            copy(
                backImageUri = "simulated_back_id.png",
                isBackUploaded = true,
                errorMessage = null
            )
        }
    }

    private fun submitDocument() {
        val state = currentState
        if (state.documentNumber.isBlank()) {
            setState { copy(errorMessage = "Please enter your document number.") }
            return
        }
        if (!state.isFrontUploaded) {
            setState { copy(errorMessage = "Please upload the front photo of your ID document.") }
            return
        }

        setState { copy(isLoading = true) }

        viewModelScope.launch {
            val doc = KycDocument(
                documentType = state.selectedDocumentType,
                documentNumber = state.documentNumber,
                frontImageUri = state.frontImageUri,
                backImageUri = state.backImageUri
            )
            kycRepository.saveDocument(doc).onSuccess {
                setState { copy(isLoading = false) }
                setEffect { KycDocumentUploadEffect.NavigateToSelfie }
            }
        }
    }
}
