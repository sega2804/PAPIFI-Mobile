package com.papifi.model

import kotlinx.serialization.Serializable

@Serializable
enum class KycStatus {
    NOT_STARTED,
    PENDING,
    VERIFIED,
    REJECTED
}
