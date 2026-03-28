package com.vnote.mobile.api

data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val fullName: String, val username: String, val password: String)
// Add the userId to this class!
data class AuthResponse(val token: String?, val message: String?, val userId: Int?)
data class NoteResponse(
    val id: Long,
    val title: String,
    val content: String
)
data class UserResponse(val userId: Long, val fullName: String, val username: String)
data class UpdateProfileRequest(val fullName: String, val username: String)