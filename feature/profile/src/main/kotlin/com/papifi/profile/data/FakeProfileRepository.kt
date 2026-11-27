package com.papifi.profile.data

import com.papifi.common.Result
import com.papifi.model.KycStatus
import com.papifi.profile.domain.ProfileRepository
import com.papifi.profile.domain.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeProfileRepository : ProfileRepository {

    private val profileState = MutableStateFlow(
        UserProfile(
            id = "USR-987654",
            name = "Alex Johnson",
            email = "alex.johnson@papifi.com",
            phone = "+234 801 234 5678",
            address = "12 Greenfield Estate, Victoria Island, Lagos",
            kycTier = 1,
            kycStatus = KycStatus.NOT_STARTED,
            joinedDate = "January 15, 2024",
            isBiometricsEnabled = true,
            isNotificationsEnabled = true
        )
    )

    override fun getUserProfile(): Flow<Result<UserProfile>> {
        return profileState.asStateFlow().map { Result.Success(it) }
    }

    override suspend fun updateBiometrics(enabled: Boolean): Result<Unit> {
        profileState.value = profileState.value.copy(isBiometricsEnabled = enabled)
        return Result.Success(Unit)
    }

    override suspend fun updateNotifications(enabled: Boolean): Result<Unit> {
        profileState.value = profileState.value.copy(isNotificationsEnabled = enabled)
        return Result.Success(Unit)
    }

    override suspend fun changePin(oldPin: String, newPin: String): Result<Unit> {
        return if (oldPin.length == 4 && newPin.length == 4) {
            Result.Success(Unit)
        } else {
            Result.Error(IllegalArgumentException("PIN must be 4 digits"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return Result.Success(Unit)
    }
}
