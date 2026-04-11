package com.vnote.mobile.features.register

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vnote.mobile.R

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var presenter: RegisterPresenter

    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etRegisterName)
        etUsername = findViewById(R.id.etRegisterUsername)
        etPassword = findViewById(R.id.etRegisterPassword)
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)
        progressBar = findViewById(R.id.progressBarRegister)

        // Initialize Presenter
        presenter = RegisterPresenter(this, RegisterModel())

        // Delegate clicks to Presenter
        tvGoToLogin.setOnClickListener {
            presenter.onLoginClicked()
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            presenter.onRegisterClicked(name, username, password, confirmPassword)
        }
    }

    // --- MVP VIEW IMPLEMENTATIONS ---

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false
        btnRegister.text = "Creating Account..."
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        btnRegister.isEnabled = true
        btnRegister.text = "Register"
    }

    override fun showSuccess() {
        Toast.makeText(this, "Registration Successful! Please Log In.", Toast.LENGTH_LONG).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToLogin() {
        finish() // Closes this screen and returns to Login
    }
}