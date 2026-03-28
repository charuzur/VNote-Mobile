package com.vnote.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.vnote.mobile.api.RetrofitClient
import com.vnote.mobile.api.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Fetch real data from TiDB
        fetchProfileData()

        findViewById<MaterialCardView>(R.id.btnGoToEditProfile).setOnClickListener {
            startActivity(Intent(this, UpdateProfileActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnGoToChangePassword).setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
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

    private fun fetchProfileData() {
        val sharedPref = getSharedPreferences("APP", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("TOKEN", "") ?: ""

        RetrofitClient.instance.getUserProfile(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()
                    findViewById<TextView>(R.id.tvProfileName).text = user?.fullName
                    findViewById<TextView>(R.id.tvProfileUsername).text = "@${user?.username}"
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Profile Sync Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}