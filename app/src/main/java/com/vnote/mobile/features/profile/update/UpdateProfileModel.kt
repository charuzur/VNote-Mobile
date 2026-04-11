package com.vnote.mobile.features.profile.update

import com.vnote.mobile.core.network.RetrofitClient
import com.vnote.mobile.core.network.UpdateProfileRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfileModel {
    fun updateProfile(userId: String, request: UpdateProfileRequest, callback: (Boolean, String?) -> Unit) {
        RetrofitClient.instance.updateProfile(userId, request).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    callback(true, "Profile Updated!")
                } else {
                    callback(false, "Failed to update profile")
                }
            }
            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                callback(false, "Network Error")
            }
        })
    }
}