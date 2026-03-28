package com.vnote.mobile

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class UpdateProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        // Wire up the back arrow
        val ivBackUpdate = findViewById<ImageView>(R.id.ivBackUpdate)
        ivBackUpdate.setOnClickListener {
            finish() // Closes this screen and returns to Profile
        }
    }
}