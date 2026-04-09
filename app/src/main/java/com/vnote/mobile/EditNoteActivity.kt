package com.vnote.mobile

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.vnote.mobile.api.NoteRequest
import com.vnote.mobile.api.NoteResponse
import com.vnote.mobile.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditNoteActivity : AppCompatActivity() {

    private var currentMode = "CREATE"
    private var noteId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvActionBtn = findViewById<TextView>(R.id.tvSave)
        val tvScreenTitle = findViewById<TextView>(R.id.tvScreenTitle)
        val cardScreenTitle = findViewById<MaterialCardView>(R.id.cardScreenTitle) // NEW: Grabbing the card view!
        val etTitle = findViewById<EditText>(R.id.etNoteTitle)
        val etContent = findViewById<EditText>(R.id.etNoteContent)

        currentMode = intent.getStringExtra("MODE") ?: "CREATE"
        noteId = intent.getLongExtra("NOTE_ID", -1L)

        if (currentMode == "VIEW" || currentMode == "EDIT") {
            etTitle.setText(intent.getStringExtra("NOTE_TITLE"))
            etContent.setText(intent.getStringExtra("NOTE_CONTENT"))
        }

        // Pass the cardScreenTitle to our UI updater
        updateUI(currentMode, tvScreenTitle, cardScreenTitle, tvActionBtn, etTitle, etContent)

        tvActionBtn.setOnClickListener {
            if (currentMode == "VIEW") {
                currentMode = "EDIT"
                updateUI(currentMode, tvScreenTitle, cardScreenTitle, tvActionBtn, etTitle, etContent)
            } else if (currentMode == "EDIT") {
                AlertDialog.Builder(this)
                    .setTitle("Save Changes")
                    .setMessage("Are you sure you want to save these changes to your note?")
                    .setPositiveButton("Save") { _, _ ->
                        saveNote(etTitle.text.toString().trim(), etContent.text.toString().trim())
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                saveNote(etTitle.text.toString().trim(), etContent.text.toString().trim())
            }
        }

        ivBack.setOnClickListener { handleBackPress() }

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })
    }

    private fun handleBackPress() {
        if (currentMode == "CREATE" || currentMode == "EDIT") {
            AlertDialog.Builder(this)
                .setTitle("Discard Changes")
                .setMessage("Are you sure you want to go back? Any unsaved changes will be lost.")
                .setPositiveButton("Discard") { _, _ ->
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            finish()
        }
    }

    private fun saveNote(title: String, content: String) {
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPref = getSharedPreferences("APP", Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", "") ?: ""
        val userId = token.toLongOrNull()

        if (userId == null) {
            Toast.makeText(this, "Session error", Toast.LENGTH_SHORT).show()
            return
        }

        val bearerToken = "Bearer $token"
        val request = NoteRequest(title, content, userId)

        if (currentMode == "CREATE") {
            RetrofitClient.instance.createNote(bearerToken, request).enqueue(object : Callback<NoteResponse> {
                override fun onResponse(call: Call<NoteResponse>, response: Response<NoteResponse>) {
                    if (response.isSuccessful) {
                        finish()
                    } else {
                        Toast.makeText(this@EditNoteActivity, "Error creating note", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                    Toast.makeText(this@EditNoteActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
        } else if (currentMode == "EDIT") {
            RetrofitClient.instance.updateNote(bearerToken, noteId, request).enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditNoteActivity, "Note Saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditNoteActivity, "Failed to save. Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Toast.makeText(this@EditNoteActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // UPDATED: This now accepts the MaterialCardView and changes its background color dynamically!
    private fun updateUI(mode: String, title: TextView, cardTitle: MaterialCardView, actionBtn: TextView, etTitle: EditText, etContent: EditText) {
        when (mode) {
            "CREATE" -> {
                title.text = "NEW NOTE"
                cardTitle.setCardBackgroundColor(Color.parseColor("#007bff")) // Blue
                actionBtn.text = "Save"
                setEditable(true, etTitle, etContent)
            }
            "VIEW" -> {
                title.text = "VIEW NOTE"
                cardTitle.setCardBackgroundColor(Color.parseColor("#6c757d")) // Grey
                actionBtn.text = "Edit"
                setEditable(false, etTitle, etContent)
            }
            "EDIT" -> {
                title.text = "EDIT NOTE"
                cardTitle.setCardBackgroundColor(Color.parseColor("#007bff")) // Blue
                actionBtn.text = "Save"
                setEditable(true, etTitle, etContent)
            }
        }
    }

    private fun setEditable(isEditable: Boolean, vararg editTexts: EditText) {
        for (et in editTexts) {
            et.isFocusable = isEditable
            et.isFocusableInTouchMode = isEditable
            et.isCursorVisible = isEditable
            if (!isEditable) et.clearFocus()
        }
    }
}