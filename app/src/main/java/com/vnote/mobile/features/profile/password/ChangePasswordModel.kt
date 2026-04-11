package com.vnote.mobile.features.profile.password

import com.vnote.mobile.core.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordModel {
    fun changePassword(userId: String, passwords: Map<String, String>, callback: (Boolean, String?) -> Unit) {
        RetrofitClient.instance.changePassword(userId, passwords).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    callback(true, "Password Changed!")
                } else {
                    callback(false, "Incorrect Old Password")
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                callback(false, "Network Error")
            }
        })
    }
}