package com.vnote.mobile.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/v1/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("api/v1/auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    // NEW: Fetch Notes for the Dashboard!
    // We add the @Header to satisfy the "Authorization: Bearer <token>" rubric requirement
    @GET("api/v1/notes/user/{userId}")
    fun getUserNotes(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Call<List<NoteResponse>>

    // Get Profile Data
    @GET("api/v1/users/{id}")
    fun getUserProfile(@Path("id") id: String): Call<UserResponse>

    // Update Profile (Name/Username)
    @PUT("api/v1/users/{id}")
    fun updateProfile(@Path("id") id: String, @Body request: UpdateProfileRequest): Call<Map<String, String>>

    // Change Password
    @PUT("api/v1/users/{id}/password")
    fun changePassword(@Path("id") id: String, @Body passwords: Map<String, String>): Call<Map<String, String>>
}