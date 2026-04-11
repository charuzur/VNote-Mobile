package com.vnote.mobile.features.profile.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.vnote.mobile.R
import com.vnote.mobile.features.dashboard.MainActivity
import com.vnote.mobile.features.login.LoginActivity
import com.vnote.mobile.features.profile.password.ChangePasswordActivity
import com.vnote.mobile.features.profile.update.UpdateProfileActivity
import java.io.File

class ProfileActivity : AppCompatActivity(), ProfileContract.View {

    private lateinit var presenter: ProfilePresenter

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileUsername: TextView
    private lateinit var tvMemberSince: TextView
    private lateinit var tvNotesTotalCount: TextView
    private lateinit var ivProfilePicture: ImageView

    // Setup the Gallery Image Picker
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = getFileFromUri(it)
            presenter.onImagePicked(file)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileUsername = findViewById(R.id.tvProfileUsername)
        tvMemberSince = findViewById(R.id.tvMemberSince)
        tvNotesTotalCount = findViewById(R.id.tvNotesTotalCount)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)

        presenter = ProfilePresenter(this, ProfileModel())

        // Click Listeners -> Delegated to Presenter
        findViewById<ImageView>(R.id.ivEditProfilePic).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        findViewById<MaterialCardView>(R.id.btnGoToEditProfile).setOnClickListener {
            presenter.onEditProfileClicked()
        }

        findViewById<MaterialCardView>(R.id.btnGoToChangePassword).setOnClickListener {
            presenter.onChangePasswordClicked()
        }

        findViewById<MaterialCardView>(R.id.btnLogout).setOnClickListener {
            presenter.onLogoutClicked()
        }

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationViewProfile)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_home) {
                presenter.onHomeNavClicked()
                true
            } else true
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.loadProfile()
    }

    // --- MVP VIEW IMPLEMENTATIONS ---

    override fun getToken(): String {
        return getSharedPreferences("APP", Context.MODE_PRIVATE).getString("TOKEN", "") ?: ""
    }

    override fun showProfileData(name: String, username: String, memberSince: String) {
        tvProfileName.text = name
        tvProfileUsername.text = "@$username"
        if (memberSince.isNotEmpty()) {
            tvMemberSince.text = memberSince
        }
    }

    override fun showProfileImage(base64String: String?) {
        if (!base64String.isNullOrEmpty()) {
            try {
                val imageBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
                val decodedImage = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ivProfilePicture.setImageBitmap(decodedImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun showNotesCount(count: Int) {
        tvNotesTotalCount.text = count.toString()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out of your account?")
            .setPositiveButton("Log Out") { _, _ -> presenter.confirmLogout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun navigateToDashboard() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun navigateToEditProfile(currentName: String, currentUsername: String) {
        val intent = Intent(this, UpdateProfileActivity::class.java)
        intent.putExtra("CURRENT_NAME", currentName)
        intent.putExtra("CURRENT_USERNAME", currentUsername)
        startActivity(intent)
    }

    override fun navigateToChangePassword() {
        startActivity(Intent(this, ChangePasswordActivity::class.java))
    }

    // Utility function to convert Uri to File, keeping the Presenter pure!
    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
            tempFile.outputStream().use { inputStream?.copyTo(it) }
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}