package com.papifi.profile.domain

import com.papifi.common.Result
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile(): Flow<Result<UserProfile>>
    suspend fun updateBiometrics(enabled: Boolean): Result<Unit>
    suspend fun updateNotifications(enabled: Boolean): Result<Unit>
    suspend fun changePin(oldPin: String, newPin: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}
