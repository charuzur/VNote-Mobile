package com.vnote.mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vnote.mobile.api.NoteResponse

class NoteAdapter(
    var notesList: MutableList<NoteResponse>, // Changed to NoteResponse
    private val onNoteClick: (NoteResponse) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvNoteTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvNoteDate) // Using this for content preview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesList[position]
        holder.tvTitle.text = note.title

        // Showing a preview of the content since our backend doesn't send a formatted date yet
        holder.tvDate.text = if (note.content.length > 20) note.content.take(20) + "..." else note.content

        holder.itemView.setOnClickListener {
            onNoteClick(note)
        }
    }

    override fun getItemCount(): Int = notesList.size
}