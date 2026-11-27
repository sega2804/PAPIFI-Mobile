package com.papifi.kyc.domain

data class KycDocument(
    val documentType: KycDocumentType,
    val documentNumber: String,
    val frontImageUri: String? = null,
    val backImageUri: String? = null
)
