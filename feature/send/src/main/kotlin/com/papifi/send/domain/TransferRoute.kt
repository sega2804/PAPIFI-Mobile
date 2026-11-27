package com.papifi.send.domain

import kotlinx.serialization.Serializable

@Serializable
enum class TransferRoute {
    LOCAL_BANK,
    INTERNATIONAL,
    PAPIFI_WALLET
}
