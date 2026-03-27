package com.vnote.mobile

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val notesList = mutableListOf(
        Note("Project Idea", "Feb 6"),
        Note("Grocery List", "Feb 5"),
        Note("Meeting with Kent", "Feb 2")
    )
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setup the + Button (Mode: CREATE)
        val fabCreateNote = findViewById<FloatingActionButton>(R.id.fabCreateNote)
        fabCreateNote.setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("MODE", "CREATE") // Tell the screen to act as a creator
            startActivity(intent)
        }

        // 2. Setup the List and Handle Card Clicks (Mode: VIEW)
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)
        adapter = NoteAdapter(notesList) { clickedNote ->
            val intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("MODE", "VIEW") // Tell the screen to be read-only
            startActivity(intent)
        }

        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        // 3. Attach Swipe-to-Delete functionality
        setupSwipeToDelete(rvNotes)
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView) {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Delete") { _, _ ->
                        notesList.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        android.widget.Toast.makeText(this@MainActivity, "Note deleted", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
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
                        // 1. Draw Red Background
                        val backgroundRect = RectF(bgLeft, bgTop, bgRight, bgBottom)
                        c.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, p)

                        // 2. Setup the Text properties
                        val textPaint = Paint().apply {
                            color = Color.WHITE
                            textSize = 12 * density // 12sp text size
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }

                        // 3. Draw Icon and Text
                        val icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete)
                        icon?.let {
                            it.setTint(Color.WHITE)

                            val iconSize = (28 * density).toInt()
                            val textGap = (4 * density).toInt() // Small gap between icon and text
                            val textHeight = textPaint.descent() - textPaint.ascent()

                            // Calculate total height so we can perfectly center the block
                            val totalContentHeight = iconSize + textGap + textHeight
                            val contentTop = itemView.top + (itemView.height - totalContentHeight) / 2

                            val iconTop = contentTop.toInt()
                            val iconBottom = iconTop + iconSize

                            val paddingFromEdge = (24 * density).toInt()
                            val iconRight = (itemView.right - marginHorizontal) - paddingFromEdge
                            val iconLeft = iconRight - iconSize

                            // Draw them as soon as the red box is wide enough
                            if (bgLeft < iconLeft - 20) {
                                // Draw Icon
                                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                                it.draw(c)

                                // Draw Text directly underneath
                                val textX = iconLeft + (iconSize / 2f) // Center text horizontally with icon
                                val textY = iconBottom + textGap - textPaint.ascent() // Baseline for drawing text
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