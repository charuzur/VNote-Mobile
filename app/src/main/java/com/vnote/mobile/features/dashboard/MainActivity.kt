package com.vnote.mobile.features.dashboard

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vnote.mobile.R
import com.vnote.mobile.core.network.NoteResponse
import com.vnote.mobile.features.login.LoginActivity
import com.vnote.mobile.features.note_edit.EditNoteActivity
import com.vnote.mobile.features.profile.main.ProfileActivity

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var presenter: MainPresenter
    private lateinit var adapter: NoteAdapter
    private lateinit var tvNoteCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvNoteCount = findViewById(R.id.tvNoteCount)
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)

        presenter = MainPresenter(this, MainModel())

        adapter = NoteAdapter(mutableListOf()) { clickedNote ->
            presenter.onNoteClicked(clickedNote)
        }
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        // Search Bar
        val etSearch = findViewById<android.widget.EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                presenter.onSearchQueryChanged(s.toString().lowercase())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Buttons & Nav
        findViewById<FloatingActionButton>(R.id.fabCreateNote).setOnClickListener {
            presenter.onCreateNoteClicked()
        }

        findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    presenter.onProfileMenuClicked()
                    true
                }
                else -> false
            }
        }

        setupSwipeToDelete(rvNotes)
    }

    override fun onResume() {
        super.onResume()
        presenter.loadNotes()
    }

    // --- MVP VIEW IMPLEMENTATIONS ---

    override fun getToken(): String {
        return getSharedPreferences("APP", Context.MODE_PRIVATE).getString("TOKEN", "") ?: ""
    }

    override fun showNotes(notes: List<NoteResponse>) {
        adapter.updateList(notes)
    }

    override fun updateNoteCount(count: Int) {
        tvNoteCount.text = count.toString()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSessionExpired() {
        Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun navigateToCreateNote() {
        val intent = Intent(this, EditNoteActivity::class.java)
        intent.putExtra("MODE", "CREATE")
        startActivity(intent)
    }

    override fun navigateToEditNote(note: NoteResponse) {
        val intent = Intent(this, EditNoteActivity::class.java)
        intent.putExtra("MODE", "VIEW")
        intent.putExtra("NOTE_ID", note.noteId)
        intent.putExtra("NOTE_TITLE", note.title)
        intent.putExtra("NOTE_CONTENT", note.content)
        startActivity(intent)
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun showDeleteConfirmation(note: NoteResponse, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to permanently delete this note?")
            .setPositiveButton("Delete") { _, _ -> presenter.confirmDeleteNote(note, position) }
            .setNegativeButton("Cancel") { _, _ -> presenter.cancelDeleteNote(position) }
            .setCancelable(false)
            .show()
    }

    override fun removeNoteFromList(position: Int) {
        adapter.notesList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    override fun resetSwipeState(position: Int) {
        adapter.notifyItemChanged(position)
    }

    // --- UI DRAWING FOR SWIPE (UNCHANGED) ---
    private fun setupSwipeToDelete(recyclerView: RecyclerView) {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val noteToDelete = adapter.notesList[position]
                presenter.onNoteSwiped(noteToDelete, position)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView
                val density = resources.displayMetrics.density

                val marginHorizontal = (24 * density).toInt()
                val marginVertical = (8 * density).toInt()
                val cornerRadius = 12 * density

                if (dX < 0) {
                    val p = Paint().apply { color = Color.parseColor("#FF3B30") }

                    val bgLeft = itemView.right + dX
                    val bgRight = (itemView.right - marginHorizontal).toFloat()
                    val bgTop = (itemView.top + marginVertical).toFloat()
                    val bgBottom = (itemView.bottom - marginVertical).toFloat()

                    if (bgLeft < bgRight) {
                        val backgroundRect = RectF(bgLeft, bgTop, bgRight, bgBottom)
                        c.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, p)

                        val textPaint = Paint().apply {
                            color = Color.WHITE
                            textSize = 12 * density
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }

                        val icon = ContextCompat.getDrawable(this@MainActivity, android.R.drawable.ic_menu_delete)
                        icon?.let {
                            it.setTint(Color.WHITE)

                            val iconSize = (28 * density).toInt()
                            val textGap = (4 * density).toInt()
                            val textHeight = textPaint.descent() - textPaint.ascent()

                            val totalContentHeight = iconSize + textGap + textHeight
                            val contentTop = itemView.top + (itemView.height - totalContentHeight) / 2

                            val iconTop = contentTop.toInt()
                            val iconBottom = iconTop + iconSize

                            val paddingFromEdge = (24 * density).toInt()
                            val iconRight = (itemView.right - marginHorizontal) - paddingFromEdge
                            val iconLeft = iconRight - iconSize

                            if (bgLeft < iconLeft - 20) {
                                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                                it.draw(c)

                                val textX = iconLeft + (iconSize / 2f)
                                val textY = iconBottom + textGap - textPaint.ascent()
                                c.drawText("Delete", textX, textY, textPaint)
                            }
                        }
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }
}