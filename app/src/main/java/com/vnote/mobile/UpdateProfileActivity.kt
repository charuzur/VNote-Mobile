package com.vnote.mobile

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.vnote.mobile.api.RetrofitClient
import com.vnote.mobile.api.UpdateProfileRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        val etName = findViewById<EditText>(R.id.etUpdateName)
        val etUsername = findViewById<EditText>(R.id.etUpdateUsername)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)
        val ivBack = findViewById<ImageView>(R.id.ivBackUpdateProfile)

        // Get the current data passed from ProfileActivity and pre-fill the boxes
        val currentName = intent.getStringExtra("CURRENT_NAME") ?: ""
        val currentUsername = intent.getStringExtra("CURRENT_USERNAME") ?: ""
        etName.setText(currentName)
        etUsername.setText(currentUsername)

        ivBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            // If they leave it blank, fall back to the old value so we don't break the DB
            val newName = etName.text.toString().trim().ifEmpty { currentName }
            val newUsername = etUsername.text.toString().trim().ifEmpty { currentUsername }

            AlertDialog.Builder(this)
                .setTitle("Save Changes")
                .setMessage("Are you sure you want to update your profile information?")
                .setPositiveButton("Save") { _, _ ->
                    executeUpdate(newName, newUsername)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun executeUpdate(newName: String, newUsername: String) {
        val sharedPref = getSharedPreferences("APP", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("TOKEN", "") ?: ""

        val request = UpdateProfileRequest(newName, newUsername)

        RetrofitClient.instance.updateProfile(userId, request).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateProfileActivity, "Profile Updated!", Toast.LENGTH_SHORT).show()
                    finish() // Triggers onResume() in ProfileActivity to refresh the UI!
                } else {
                    Toast.makeText(this@UpdateProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(this@UpdateProfileActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}