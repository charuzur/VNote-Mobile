package com.vnote.mobile.features.note_edit

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
import com.vnote.mobile.R

class EditNoteActivity : AppCompatActivity(), EditNoteContract.View {

    private lateinit var presenter: EditNotePresenter

    private lateinit var tvScreenTitle: TextView
    private lateinit var cardScreenTitle: MaterialCardView
    private lateinit var tvActionBtn: TextView
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        tvScreenTitle = findViewById(R.id.tvScreenTitle)
        cardScreenTitle = findViewById(R.id.cardScreenTitle)
        tvActionBtn = findViewById(R.id.tvSave)
        etTitle = findViewById(R.id.etNoteTitle)
        etContent = findViewById(R.id.etNoteContent)

        presenter = EditNotePresenter(this, EditNoteModel())

        val mode = intent.getStringExtra("MODE") ?: "CREATE"
        val noteId = intent.getLongExtra("NOTE_ID", -1L)

        if (mode == "VIEW" || mode == "EDIT") {
            etTitle.setText(intent.getStringExtra("NOTE_TITLE"))
            etContent.setText(intent.getStringExtra("NOTE_CONTENT"))
        }

        presenter.initialize(mode, noteId)

        tvActionBtn.setOnClickListener {
            presenter.onActionButtonClicked(
                etTitle.text.toString().trim(),
                etContent.text.toString().trim()
            )
        }

        ivBack.setOnClickListener { presenter.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                presenter.onBackPressed()
            }
        })
    }

    // --- MVP VIEW IMPLEMENTATIONS ---

    override fun getToken(): String {
        return getSharedPreferences("APP", Context.MODE_PRIVATE).getString("TOKEN", "") ?: ""
    }

    override fun getUserId(): Long? {
        val token = getToken()
        return token.toLongOrNull()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun closeScreen() {
        finish()
    }

    override fun showSaveConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Save Changes")
            .setMessage("Are you sure you want to save these changes to your note?")
            .setPositiveButton("Save") { _, _ ->
                presenter.onSaveConfirmed(
                    etTitle.text.toString().trim(),
                    etContent.text.toString().trim()
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun showDiscardConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Discard Changes")
            .setMessage("Are you sure you want to go back? Any unsaved changes will be lost.")
            .setPositiveButton("Discard") { _, _ -> finish() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun setUIForMode(mode: String) {
        when (mode) {
            "CREATE" -> {
                tvScreenTitle.text = "NEW NOTE"
                cardScreenTitle.setCardBackgroundColor(Color.parseColor("#007bff"))
                tvActionBtn.text = "Save"
                setEditable(true, etTitle, etContent)
            }
            "VIEW" -> {
                tvScreenTitle.text = "VIEW NOTE"
                cardScreenTitle.setCardBackgroundColor(Color.parseColor("#6c757d"))
                tvActionBtn.text = "Edit"
                setEditable(false, etTitle, etContent)
            }
            "EDIT" -> {
                tvScreenTitle.text = "EDIT NOTE"
                cardScreenTitle.setCardBackgroundColor(Color.parseColor("#007bff"))
                tvActionBtn.text = "Save"
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