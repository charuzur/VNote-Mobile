package com.vnote.mobile

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.vnote.mobile.api.NoteResponse
import com.vnote.mobile.api.RetrofitClient
import com.vnote.mobile.api.UserResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private var currentName = ""
    private var currentUsername = ""

    // 1. Setup the Gallery Image Picker
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadImageToBackend(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Launch the gallery when tapping the edit badge
        findViewById<ImageView>(R.id.ivEditProfilePic).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        findViewById<MaterialCardView>(R.id.btnGoToEditProfile).setOnClickListener {
            val intent = Intent(this, UpdateProfileActivity::class.java)
            intent.putExtra("CURRENT_NAME", currentName)
            intent.putExtra("CURRENT_USERNAME", currentUsername)
            startActivity(intent)
        }

        findViewById<MaterialCardView>(R.id.btnGoToChangePassword).setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnLogout).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of your account?")
                .setPositiveButton("Log Out") { _, _ ->
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationViewProfile)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_home) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            } else true
        }
    }

    override fun onResume() {
        super.onResume()
        fetchProfileData()
    }

    private fun fetchProfileData() {
        val sharedPref = getSharedPreferences("APP", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("TOKEN", "") ?: ""

        if (userId.isEmpty()) return

        // Fetch real-time Notes count
        fetchNotesCount(userId)

        RetrofitClient.instance.getUserProfile(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()
                    currentName = user?.fullName ?: ""
                    currentUsername = user?.username ?: ""

                    findViewById<TextView>(R.id.tvProfileName).text = currentName
                    findViewById<TextView>(R.id.tvProfileUsername).text = "@$currentUsername"

                    // --- NEW: Format and set the "Member Since" date ---
                    if (!user?.createdAt.isNullOrEmpty()) {
                        try {
                            // Spring Boot sends ISO format, e.g., "2026-03-28T12:40:02"
                            val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                            // We want to display "Mar 2026"
                            val formatter = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())

                            val date = parser.parse(user?.createdAt!!)
                            val formattedDate = date?.let { formatter.format(it) } ?: ""

                            findViewById<TextView>(R.id.tvMemberSince).text = "Member since $formattedDate"
                        } catch (e: Exception) {
                            // If parsing fails, just leave it blank or set a fallback
                            e.printStackTrace()
                        }
                    }

                    // Decode Base64 Image to Bitmap (Existing code)
                    if (!user?.profileImage.isNullOrEmpty()) {
                        try {
                            val imageBytes = android.util.Base64.decode(user?.profileImage, android.util.Base64.DEFAULT)
                            val decodedImage = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            findViewById<ImageView>(R.id.ivProfilePicture).setImageBitmap(decodedImage)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Profile Sync Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchNotesCount(userId: String) {
        val bearerToken = "Bearer $userId"
        RetrofitClient.instance.getUserNotes(bearerToken, userId).enqueue(object : Callback<List<NoteResponse>> {
            override fun onResponse(call: Call<List<NoteResponse>>, response: Response<List<NoteResponse>>) {
                if (response.isSuccessful) {
                    val totalNotes = response.body()?.size ?: 0
                    findViewById<TextView>(R.id.tvNotesTotalCount).text = totalNotes.toString()
                }
            }
            override fun onFailure(call: Call<List<NoteResponse>>, t: Throwable) {
                findViewById<TextView>(R.id.tvNotesTotalCount).text = "0"
            }
        })
    }

    private fun uploadImageToBackend(uri: Uri) {
        val sharedPref = getSharedPreferences("APP", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("TOKEN", "") ?: ""

        try {
            // Convert Uri to a temporary file Spring Boot can read
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
            tempFile.outputStream().use { inputStream?.copyTo(it) }

            val requestFile = RequestBody.create(MediaType.parse("image/*"), tempFile)
            val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

            // Make the API Call
            RetrofitClient.instance.uploadPhoto(userId, body).enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfileActivity, "Photo uploaded!", Toast.LENGTH_SHORT).show()
                        fetchProfileData() // Refresh to show new photo!
                    } else {
                        Toast.makeText(this@ProfileActivity, "Upload failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
        }
    }
}