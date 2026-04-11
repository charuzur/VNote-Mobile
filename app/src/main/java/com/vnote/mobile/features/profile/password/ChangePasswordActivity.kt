package com.vnote.mobile.features.profile.password

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.vnote.mobile.R

class ChangePasswordActivity : AppCompatActivity(), ChangePasswordContract.View {

    private lateinit var presenter: ChangePasswordPresenter

    private lateinit var etOld: EditText
    private lateinit var etNew: EditText
    private lateinit var etConfirm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        etOld = findViewById(R.id.etCurrentPassword)
        etNew = findViewById(R.id.etNewPassword)
        etConfirm = findViewById(R.id.etConfirmNewPassword)
        val btnSave = findViewById<Button>(R.id.btnUpdatePassword)
        val ivBack = findViewById<ImageView>(R.id.ivBackPassword)

        presenter = ChangePasswordPresenter(this, ChangePasswordModel())

        ivBack.setOnClickListener {
            presenter.onBackClicked()
        }

        btnSave.setOnClickListener {
            presenter.onSaveClicked(
                etOld.text.toString(),
                etNew.text.toString(),
                etConfirm.text.toString()
            )
        }
    }

    // --- MVP VIEW IMPLEMENTATIONS ---

    override fun getToken(): String {
        return getSharedPreferences("APP", Context.MODE_PRIVATE).getString("TOKEN", "") ?: ""
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showUpdateConfirmation(oldPass: String, newPass: String) {
        AlertDialog.Builder(this)
            .setTitle("Update Password")
            .setMessage("Are you sure you want to change your password?")
            .setPositiveButton("Update") { _, _ ->
                presenter.onUpdateConfirmed(oldPass, newPass)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun closeScreen() {
        finish()
    }
}