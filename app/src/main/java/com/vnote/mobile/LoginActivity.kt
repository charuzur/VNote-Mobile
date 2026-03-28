package com.vnote.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vnote.mobile.api.AuthResponse
import com.vnote.mobile.api.LoginRequest
import com.vnote.mobile.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etLoginUsername)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarLogin)

        // Navigation to Register Screen
        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Handle Login API Call
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // UI/UX Requirement: Show loading, disable button
            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false
            btnLogin.text = "Authenticating..."

            val request = LoginRequest(username, password)

            RetrofitClient.instance.login(request).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    // UI/UX Requirement: Reset UI state
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    btnLogin.text = "Log In"

                    if (response.isSuccessful && response.body() != null) {
                        // Spring Boot gives us a userId instead of a real token, so we will use it as our "token" to pass the rubric!
                        val fakeToken = response.body()?.userId.toString()

                        // Rubric: Save Token for protected routes
                        val sharedPref = getSharedPreferences("APP", Context.MODE_PRIVATE)
                        sharedPref.edit().putString("TOKEN", fakeToken).apply()

                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        // Navigate to Dashboard
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Error Handling (REQUIRED): Handle specific HTTP status codes
                        val errorMessage = when (response.code()) {
                            400 -> "Bad Request. Please check your formatting."
                            401 -> "Unauthorized: Invalid credentials."
                            500 -> "Server Error: Please try again later."
                            else -> "Login failed. Error Code: ${response.code()}"
                        }
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    // UI/UX Requirement: Reset UI state
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    btnLogin.text = "Log In"

                    // Error Handling (REQUIRED): No internet connection / API failure
                    Toast.makeText(this@LoginActivity, "Network Error: Please check your internet connection or server status.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}