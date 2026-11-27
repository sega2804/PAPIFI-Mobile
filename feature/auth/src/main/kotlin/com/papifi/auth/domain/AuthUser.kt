package com.papifi.auth.domain

data class AuthUser(
    val id: String,
    val email: String,
    val phoneNumber: String,
    val name: String,
    val isVerified: Boolean = false,
    val pinCreated: Boolean = false
)
