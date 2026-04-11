package com.vnote.mobile.features.register

import com.vnote.mobile.core.network.AuthResponse
import com.vnote.mobile.core.network.RegisterRequest
import com.vnote.mobile.core.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterModel {
    fun registerUser(name: String, username: String, password: String, callback: (Boolean, String?) -> Unit) {
        val request = RegisterRequest(fullName = name, username = username, password = password)

        RetrofitClient.instance.register(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Bad Request: Please check your formatting."
                        500 -> "Server Error: Please try again later."
                        else -> "Registration failed. Error Code: ${response.code()}"
                    }
                    callback(false, errorMessage)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                callback(false, "Network Error: Check connection.")
            }
        })
    }
}