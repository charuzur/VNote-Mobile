package com.vnote.mobile.features.dashboard

import com.vnote.mobile.core.network.NoteResponse
import com.vnote.mobile.core.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainModel {
    fun fetchNotes(token: String, callback: (Boolean, List<NoteResponse>?, Int) -> Unit) {
        val bearerToken = "Bearer $token"

        // Note: Using token as userId based on your API setup
        RetrofitClient.instance.getUserNotes(bearerToken, token).enqueue(object : Callback<List<NoteResponse>> {
            override fun onResponse(call: Call<List<NoteResponse>>, response: Response<List<NoteResponse>>) {
                if (response.isSuccessful) {
                    callback(true, response.body(), response.code())
                } else {
                    callback(false, null, response.code())
                }
            }

            override fun onFailure(call: Call<List<NoteResponse>>, t: Throwable) {
                callback(false, null, 0)
            }
        })
    }

    fun deleteNote(token: String, noteId: Long, callback: (Boolean) -> Unit) {
        val bearerToken = "Bearer $token"

        RetrofitClient.instance.deleteNote(bearerToken, noteId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                callback(response.isSuccessful)
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                callback(false)
            }
        })
    }
}