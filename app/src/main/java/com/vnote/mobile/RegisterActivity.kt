package com.vnote.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Find our input fields (Ready for the API!)
        val etName = findViewById<EditText>(R.id.etRegisterName)
        val etUsername = findViewById<EditText>(R.id.etRegisterUsername)
        val etPassword = findViewById<EditText>(R.id.etRegisterPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etRegisterConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // Find the "Log in" text and set the click listener
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)
        tvGoToLogin.setOnClickListener {
            finish()
        }

        // Setup the Register button logic stub
        btnRegister.setOnClickListener {
            // TODO: Later, we will collect the text from the inputs above
            // and send the registration data to Spring Boot here!
        }
    }
}