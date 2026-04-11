package com.vnote.mobile.features.profile.main

import com.vnote.mobile.core.network.NoteResponse
import com.vnote.mobile.core.network.RetrofitClient
import com.vnote.mobile.core.network.UserResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileModel {

    fun fetchProfile(userId: String, callback: (Boolean, UserResponse?) -> Unit) {
        RetrofitClient.instance.getUserProfile(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) callback(true, response.body())
                else callback(false, null)
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                callback(false, null)
            }
        })
    }

    fun fetchNotesCount(userId: String, callback: (Boolean, Int) -> Unit) {
        val bearerToken = "Bearer $userId"
        RetrofitClient.instance.getUserNotes(bearerToken, userId).enqueue(object : Callback<List<NoteResponse>> {
            override fun onResponse(call: Call<List<NoteResponse>>, response: Response<List<NoteResponse>>) {
                if (response.isSuccessful) callback(true, response.body()?.size ?: 0)
                else callback(false, 0)
            }
            override fun onFailure(call: Call<List<NoteResponse>>, t: Throwable) {
                callback(false, 0)
            }
        })
    }

    fun uploadPhoto(userId: String, file: File, callback: (Boolean, String?) -> Unit) {
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        RetrofitClient.instance.uploadPhoto(userId, body).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) callback(true, "Photo uploaded!")
                else callback(false, "Upload failed: ${response.code()}")
            }
            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                callback(false, "Network Error")
            }
        })
    }
}