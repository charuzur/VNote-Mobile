package com.vnote.mobile.core.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT // Added this missing import!
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.DELETE

interface ApiService {
    @POST("api/v1/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("api/v1/auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    // Fetch Notes for the Dashboard
    @GET("api/v1/notes/user/{userId}")
    fun getUserNotes(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Call<List<NoteResponse>>

    // Create a Note
    @POST("api/v1/notes")
    fun createNote(
        @Header("Authorization") token: String,
        @Body request: NoteRequest
    ): Call<NoteResponse>

    // Update a Note
    @PUT("api/v1/notes/{id}")
    fun updateNote(
        @Header("Authorization") token: String,
        @Path("id") noteId: Long,
        @Body request: NoteRequest
    ): Call<Map<String, String>>

    // Delete a Note
    @DELETE("api/v1/notes/{id}")
    fun deleteNote(
        @Header("Authorization") token: String,
        @Path("id") noteId: Long
    ): Call<Map<String, String>>

    // Get Profile Data
    @GET("api/v1/users/{id}")
    fun getUserProfile(@Path("id") id: String): Call<UserResponse>

    // Update Profile (Name/Username)
    @PUT("api/v1/users/{id}")
    fun updateProfile(@Path("id") id: String, @Body request: UpdateProfileRequest): Call<Map<String, String>>

    // Change Password
    @PUT("api/v1/users/{id}/password")
    fun changePassword(@Path("id") id: String, @Body passwords: Map<String, String>): Call<Map<String, String>>

    // Upload Photo
    @Multipart
    @POST("api/v1/users/{id}/photo")
    fun uploadPhoto(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Call<Map<String, String>>
}