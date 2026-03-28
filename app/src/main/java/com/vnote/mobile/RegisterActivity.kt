package com.vnote.mobile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vnote.mobile.api.AuthResponse
import com.vnote.mobile.api.RegisterRequest
import com.vnote.mobile.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etRegisterName)
        val etUsername = findViewById<EditText>(R.id.etRegisterUsername)
        val etPassword = findViewById<EditText>(R.id.etRegisterPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etRegisterConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarRegister)

        // Navigate back to Login
        tvGoToLogin.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // UI/UX Requirement: Show loading, disable button
            progressBar.visibility = View.VISIBLE
            btnRegister.isEnabled = false
            btnRegister.text = "Creating Account..."

            val request = RegisterRequest(fullName = name, username = username, password = password)

            RetrofitClient.instance.register(request).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    // Reset UI state
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    btnRegister.text = "Register"

                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Registration Successful! Please Log In.", Toast.LENGTH_LONG).show()
                        finish() // Return user to the Login screen
                    } else {
                        // Error Handling (REQUIRED): Handle specific HTTP status codes
                        val errorMessage = when (response.code()) {
                            400 -> "Bad Request: Please check your formatting."
                            500 -> "Server Error: Please try again later."
                            else -> "Registration failed. Error Code: ${response.code()}"
                        }
                        Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    // Reset UI state
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    btnRegister.text = "Register"
                    Toast.makeText(this@RegisterActivity, "Network Error: Check connection.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}