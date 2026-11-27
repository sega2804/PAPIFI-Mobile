package com.papifi.auth.domain

import com.papifi.common.Result

interface AuthRepository {
    suspend fun login(emailOrPhone: String, password: String): Result<AuthUser>
    suspend fun signup(name: String, email: String, password: String): Result<AuthUser>
    suspend fun verifyOtp(otp: String): Result<AuthUser>
    suspend fun sendOtp(contact: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun createPin(pin: String): Result<AuthUser>
    suspend fun biometricLogin(): Result<AuthUser>
    fun getCurrentUser(): AuthUser?
}
