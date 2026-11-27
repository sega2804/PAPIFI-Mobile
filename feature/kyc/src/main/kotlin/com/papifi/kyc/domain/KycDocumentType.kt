package com.papifi.kyc.domain

enum class KycDocumentType(val displayName: String) {
    NIN("National Identity Number (NIN)"),
    PASSPORT("International Passport"),
    DRIVERS_LICENSE("Driver's License"),
    VOTERS_CARD("Voter's Card")
}
