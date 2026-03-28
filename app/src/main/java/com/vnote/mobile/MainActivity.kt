package com.vnote.mobile

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
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

    private var notesList = mutableListOf<NoteResponse>()
    private lateinit var adapter: NoteAdapter
    private lateinit var tvNoteCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvNoteCount = findViewById(R.id.tvNoteCount)
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)

        // Setup RecyclerView
        adapter = NoteAdapter(notesList) { clickedNote ->
            val intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("MODE", "VIEW")
            intent.putExtra("NOTE_ID", clickedNote.id)
            startActivity(intent)
        }
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        // API CALL: Fetch real data from Spring Boot
        fetchNotes()

        // FAB Logic
        findViewById<FloatingActionButton>(R.id.fabCreateNote).setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("MODE", "CREATE")
            startActivity(intent)
        }

        // Nav Logic
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

    private fun fetchNotes() {
        val sharedPref = getSharedPreferences("APP", Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", "") ?: ""

        if (token.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Rubric Requirement: Authorization: Bearer <token>
        val bearerToken = "Bearer $token"

        RetrofitClient.instance.getUserNotes(bearerToken, token).enqueue(object : Callback<List<NoteResponse>> {
            override fun onResponse(call: Call<List<NoteResponse>>, response: Response<List<NoteResponse>>) {
                if (response.isSuccessful) {
                    notesList.clear()
                    response.body()?.let { notesList.addAll(it) }
                    adapter.notifyDataSetChanged()
                    tvNoteCount.text = notesList.size.toString()
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
                val noteToDelete = notesList[position]

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete") { _, _ ->
                        // In a real app, you'd call RetrofitClient.instance.deleteNote here
                        notesList.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        tvNoteCount.text = notesList.size.toString()
                    }
                    .setNegativeButton("Cancel") { _, _ -> adapter.notifyItemChanged(position) }
                    .show()
            }

            // Your existing onChildDraw code remains the same...
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }
}