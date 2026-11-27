package com.papifi.profile.domain

import com.papifi.model.KycStatus

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val kycTier: Int,
    val kycStatus: KycStatus,
    val joinedDate: String,
    val isBiometricsEnabled: Boolean = false,
    val isNotificationsEnabled: Boolean = true
)
