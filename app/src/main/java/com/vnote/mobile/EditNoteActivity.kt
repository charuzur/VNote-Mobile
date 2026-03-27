package com.vnote.mobile

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EditNoteActivity : AppCompatActivity() {

    private var currentMode = "CREATE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvActionBtn = findViewById<TextView>(R.id.tvSave)
        val tvScreenTitle = findViewById<TextView>(R.id.tvScreenTitle)
        val etTitle = findViewById<EditText>(R.id.etNoteTitle)
        val etContent = findViewById<EditText>(R.id.etNoteContent)

        // 1. Check what mode we are in (CREATE or VIEW)
        currentMode = intent.getStringExtra("MODE") ?: "CREATE"

        // 2. Setup the initial UI
        updateUI(currentMode, tvScreenTitle, tvActionBtn, etTitle, etContent)

        // 3. Handle the Top Right Button Click (Save or Edit)
        tvActionBtn.setOnClickListener {
            if (currentMode == "VIEW") {
                // User wants to edit! Switch the mode.
                currentMode = "EDIT"
                updateUI(currentMode, tvScreenTitle, tvActionBtn, etTitle, etContent)
            } else {
                // Mode is CREATE or EDIT, so they clicked "Save".
                // TODO: Save data to Spring Boot backend here later!
                finish() // Close screen and return to dashboard
            }
        }

        ivBack.setOnClickListener {
            finish()
        }
    }

    // This single function transforms the entire screen based on the current mode
    private fun updateUI(mode: String, title: TextView, actionBtn: TextView, etTitle: EditText, etContent: EditText) {
        when (mode) {
            "CREATE" -> {
                title.text = "Create a Note"
                actionBtn.text = "Save"
                setEditable(true, etTitle, etContent)
            }
            "VIEW" -> {
                title.text = "View Note"
                actionBtn.text = "Edit"
                setEditable(false, etTitle, etContent)
            }
            "EDIT" -> {
                title.text = "Edit Note"
                actionBtn.text = "Save"
                setEditable(true, etTitle, etContent)
            }
        }
    }

    // Locks or unlocks the text fields
    private fun setEditable(isEditable: Boolean, vararg editTexts: EditText) {
        for (et in editTexts) {
            et.isFocusable = isEditable
            et.isFocusableInTouchMode = isEditable
            et.isCursorVisible = isEditable
            if (!isEditable) et.clearFocus()
        }
    }
}