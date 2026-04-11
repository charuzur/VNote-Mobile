package com.vnote.mobile.features.login

import com.vnote.mobile.core.network.AuthResponse
import com.vnote.mobile.core.network.LoginRequest
import com.vnote.mobile.core.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginModel {
    fun loginUser(username: String, password: String, callback: (Boolean, String?, String?) -> Unit) {
        val request = LoginRequest(username, password)

        RetrofitClient.instance.login(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val fakeToken = response.body()?.userId.toString()
                    callback(true, fakeToken, null)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Bad Request. Please check your formatting."
                        401 -> "Unauthorized: Invalid credentials."
                        500 -> "Server Error: Please try again later."
                        else -> "Login failed. Error Code: ${response.code()}"
                    }
                    callback(false, null, errorMessage)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                callback(false, null, "Network Error: Please check your internet connection or server status.")
            }
        })
    }
}