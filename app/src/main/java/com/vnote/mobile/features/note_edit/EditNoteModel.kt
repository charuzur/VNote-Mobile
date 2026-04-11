package com.vnote.mobile.features.note_edit

import com.vnote.mobile.core.network.NoteRequest
import com.vnote.mobile.core.network.NoteResponse
import com.vnote.mobile.core.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditNoteModel {
    fun createNote(token: String, request: NoteRequest, callback: (Boolean, String?) -> Unit) {
        val bearerToken = "Bearer $token"
        RetrofitClient.instance.createNote(bearerToken, request).enqueue(object : Callback<NoteResponse> {
            override fun onResponse(call: Call<NoteResponse>, response: Response<NoteResponse>) {
                if (response.isSuccessful) callback(true, null)
                else callback(false, "Error creating note")
            }
            override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                callback(false, "Network Error")
            }
        })
    }

    fun updateNote(token: String, noteId: Long, request: NoteRequest, callback: (Boolean, String?) -> Unit) {
        val bearerToken = "Bearer $token"
        RetrofitClient.instance.updateNote(bearerToken, noteId, request).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) callback(true, "Note Saved!")
                else callback(false, "Failed to save. Error: ${response.code()}")
            }
            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                callback(false, "Network Error")
            }
        })
    }
}