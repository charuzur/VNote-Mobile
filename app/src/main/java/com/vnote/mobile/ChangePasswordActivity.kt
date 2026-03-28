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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val etOld = findViewById<EditText>(R.id.etCurrentPassword)
        val etNew = findViewById<EditText>(R.id.etNewPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmNewPassword) // Added this!
        val btnSave = findViewById<Button>(R.id.btnUpdatePassword)
        val ivBack = findViewById<ImageView>(R.id.ivBackPassword)

        ivBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val sharedPref = getSharedPreferences("APP", Context.MODE_PRIVATE)
            val userId = sharedPref.getString("TOKEN", "") ?: ""

            if (etNew.text.toString() != etConfirm.text.toString()) {
                Toast.makeText(this, "New passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Password Confirmation Dialog
            AlertDialog.Builder(this)
                .setTitle("Update Password")
                .setMessage("Are you sure you want to change your password?")
                .setPositiveButton("Update") { _, _ ->
                    val passwords = mapOf(
                        "oldPassword" to etOld.text.toString(),
                        "newPassword" to etNew.text.toString()
                    )

                    RetrofitClient.instance.changePassword(userId, passwords).enqueue(object : Callback<Map<String, String>> {
                        override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@ChangePasswordActivity, "Password Changed!", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@ChangePasswordActivity, "Incorrect Old Password", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                            Toast.makeText(this@ChangePasswordActivity, "Network Error", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}