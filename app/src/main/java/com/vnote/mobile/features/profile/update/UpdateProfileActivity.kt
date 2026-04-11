package com.vnote.mobile.features.profile.update

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.vnote.mobile.R

class UpdateProfileActivity : AppCompatActivity(), UpdateProfileContract.View {

    private lateinit var presenter: UpdateProfilePresenter

    private lateinit var etName: EditText
    private lateinit var etUsername: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        etName = findViewById(R.id.etUpdateName)
        etUsername = findViewById(R.id.etUpdateUsername)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)
        val ivBack = findViewById<ImageView>(R.id.ivBackUpdateProfile)

        presenter = UpdateProfilePresenter(this, UpdateProfileModel())

        // Get initial data and pass to presenter
        val currentName = intent.getStringExtra("CURRENT_NAME") ?: ""
        val currentUsername = intent.getStringExtra("CURRENT_USERNAME") ?: ""
        presenter.initialize(currentName, currentUsername)

        // Click Listeners
        ivBack.setOnClickListener {
            presenter.onBackClicked()
        }

        btnSave.setOnClickListener {
            presenter.onSaveClicked(
                etName.text.toString().trim(),
                etUsername.text.toString().trim()
            )
        }
    }

    // --- MVP VIEW IMPLEMENTATIONS ---

    override fun getToken(): String {
        return getSharedPreferences("APP", Context.MODE_PRIVATE).getString("TOKEN", "") ?: ""
    }

    override fun prefillData(name: String, username: String) {
        etName.setText(name)
        etUsername.setText(username)
    }

    override fun showSaveConfirmation(newName: String, newUsername: String) {
        AlertDialog.Builder(this)
            .setTitle("Save Changes")
            .setMessage("Are you sure you want to update your profile information?")
            .setPositiveButton("Save") { _, _ ->
                presenter.onSaveConfirmed(newName, newUsername)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun closeScreen() {
        finish()
    }
}