package com.vnote.mobile

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
import com.vnote.mobile.api.NoteResponse
import com.vnote.mobile.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter
    private lateinit var tvNoteCount: TextView

    // We only need ONE master list here now! The Adapter handles the filtered list.
    private var fullNotesList = mutableListOf<NoteResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvNoteCount = findViewById(R.id.tvNoteCount)
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)

        // Pass an empty list initially; we will fill it from the database
        adapter = NoteAdapter(mutableListOf()) { clickedNote ->
            val intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("MODE", "VIEW")
            intent.putExtra("NOTE_ID", clickedNote.noteId)
            intent.putExtra("NOTE_TITLE", clickedNote.title)
            intent.putExtra("NOTE_CONTENT", clickedNote.content)
            startActivity(intent)
        }
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        // Search Bar Logic
        val etSearch = findViewById<android.widget.EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString().lowercase()
                if (query.isEmpty()) {
                    adapter.updateList(fullNotesList) // Show all if empty
                } else {
                    // Filter by title or content
                    val filtered = fullNotesList.filter {
                        it.title.lowercase().contains(query) || it.content.lowercase().contains(query)
                    }
                    adapter.updateList(filtered)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Create Note Button
        findViewById<FloatingActionButton>(R.id.fabCreateNote).setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("MODE", "CREATE")
            startActivity(intent)
        }

        // Bottom Navigation
        findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        setupSwipeToDelete(rvNotes)
    }

    // Refresh the list automatically every time we return to the Dashboard
    override fun onResume() {
        super.onResume()
        fetchNotes()
    }

    private fun fetchNotes() {
        val sharedPref = getSharedPreferences("APP", Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", "") ?: ""

        if (token.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val bearerToken = "Bearer $token"

        RetrofitClient.instance.getUserNotes(bearerToken, token).enqueue(object : Callback<List<NoteResponse>> {
            override fun onResponse(call: Call<List<NoteResponse>>, response: Response<List<NoteResponse>>) {
                if (response.isSuccessful) {
                    fullNotesList.clear() // Keep master list updated
                    response.body()?.let { fullNotesList.addAll(it) }

                    adapter.updateList(fullNotesList)
                    tvNoteCount.text = fullNotesList.size.toString()
                } else if (response.code() == 401) {
                    Toast.makeText(this@MainActivity, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NoteResponse>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to load notes: Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView) {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // FIXED: We ask the adapter for the exact note at this position so search filtering doesn't break deletion
                val noteToDelete = adapter.notesList[position]

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to permanently delete this note?")
                    .setPositiveButton("Delete") { _, _ ->
                        deleteNoteFromBackend(noteToDelete.noteId, position, noteToDelete)
                    }
                    .setNegativeButton("Cancel") { _, _ -> adapter.notifyItemChanged(position) }
                    .setCancelable(false)
                    .show()
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

    private fun deleteNoteFromBackend(noteId: Long, position: Int, noteToDelete: NoteResponse) {
        val sharedPref = getSharedPreferences("APP", Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", "") ?: ""
        val bearerToken = "Bearer $token"

        RetrofitClient.instance.deleteNote(bearerToken, noteId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    // Remove the note safely from BOTH the master list and the active view list
                    fullNotesList.remove(noteToDelete)
                    adapter.notesList.removeAt(position)
                    adapter.notifyItemRemoved(position)

                    tvNoteCount.text = fullNotesList.size.toString()
                    Toast.makeText(this@MainActivity, "Note Deleted", Toast.LENGTH_SHORT).show()
                } else {
                    adapter.notifyItemChanged(position)
                    Toast.makeText(this@MainActivity, "Failed to delete note", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                adapter.notifyItemChanged(position)
                Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}