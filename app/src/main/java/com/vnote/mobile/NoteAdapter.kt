package com.vnote.mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vnote.mobile.api.NoteResponse
import java.text.SimpleDateFormat
import java.util.Locale

class NoteAdapter(
    // FIXED: Removed the word 'private' so MainActivity can access the list during swipe-to-delete!
    var notesList: MutableList<NoteResponse>,
    private val onNoteClick: (NoteResponse) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvNoteTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvNoteDate)
    }

    fun updateList(newList: List<NoteResponse>) {
        notesList = newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesList[position]
        holder.tvTitle.text = note.title

        if (!note.createdAt.isNullOrEmpty()) {
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val parsedDate = parser.parse(note.createdAt)
                holder.tvDate.text = parsedDate?.let { formatter.format(it) } ?: note.createdAt
            } catch (e: Exception) {
                holder.tvDate.text = "Unknown Date"
            }
        } else {
            holder.tvDate.text = "Just now"
        }

        holder.itemView.setOnClickListener {
            onNoteClick(note)
        }
    }

    override fun getItemCount(): Int = notesList.size
}