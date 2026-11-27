package com.papifi.auth.data

import com.papifi.auth.domain.AuthRepository
import com.papifi.auth.domain.AuthUser
import com.papifi.common.Result
import kotlinx.coroutines.delay

class FakeAuthRepository : AuthRepository {
    private var currentUserState: AuthUser? = null

    override suspend fun login(emailOrPhone: String, password: String): Result<AuthUser> {
        delay(800)
        if (emailOrPhone.isBlank() || password.isBlank()) {
            return Result.Error(IllegalArgumentException("Email/Phone and password cannot be empty."))
        }
        val user = AuthUser(
            id = "user_123",
            email = if (emailOrPhone.contains("@")) emailOrPhone else "user@papifi.com",
            phoneNumber = if (!emailOrPhone.contains("@")) emailOrPhone else "+1234567890",
            name = "Papifi User",
            isVerified = true,
            pinCreated = true
        )
        currentUserState = user
        return Result.Success(user)
    }

    override suspend fun signup(name: String, email: String, password: String): Result<AuthUser> {
        delay(800)
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.Error(IllegalArgumentException("All fields are required."))
        }
        val user = AuthUser(
            id = "user_${System.currentTimeMillis()}",
            email = email,
            phoneNumber = "",
            name = name,
            isVerified = false,
            pinCreated = false
        )
        currentUserState = user
        return Result.Success(user)
    }

    override suspend fun verifyOtp(otp: String): Result<AuthUser> {
        delay(800)
        if (otp.length != 6) {
            return Result.Error(IllegalArgumentException("OTP must be 6 digits."))
        }
        val user = (currentUserState ?: AuthUser(
            id = "user_123",
            email = "user@papifi.com",
            phoneNumber = "+1234567890",
            name = "Papifi User"
        )).copy(isVerified = true)
        currentUserState = user
        return Result.Success(user)
    }

    override suspend fun sendOtp(contact: String): Result<Unit> {
        delay(500)
        return Result.Success(Unit)
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        delay(800)
        if (email.isBlank() || !email.contains("@")) {
            return Result.Error(IllegalArgumentException("Please enter a valid email address."))
        }
        return Result.Success(Unit)
    }

    override suspend fun createPin(pin: String): Result<AuthUser> {
        delay(600)
        if (pin.length != 4) {
            return Result.Error(IllegalArgumentException("PIN must be 4 digits."))
        }
        val user = (currentUserState ?: AuthUser(
            id = "user_123",
            email = "user@papifi.com",
            phoneNumber = "+1234567890",
            name = "Papifi User",
            isVerified = true
        )).copy(pinCreated = true)
        currentUserState = user
        return Result.Success(user)
    }

    override suspend fun biometricLogin(): Result<AuthUser> {
        delay(600)
        val user = AuthUser(
            id = "user_bio_123",
            email = "biometric@papifi.com",
            phoneNumber = "+1234567890",
            name = "Biometric User",
            isVerified = true,
            pinCreated = true
        )
        currentUserState = user
        return Result.Success(user)
    }

    override fun getCurrentUser(): AuthUser? = currentUserState
}
