package com.example.stayfit20.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stayfit20.R
import com.example.stayfit20.model.NoteModel

class TaskAdapter(
    private var notes: MutableList<NoteModel>,
    private val onItemClick: (NoteModel) -> Unit,
    val onItemSwiped: (NoteModel, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.taskrecyclerview, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
        holder.itemView.setOnClickListener { onItemClick(note) }
    }

    override fun getItemCount(): Int = notes.size

    fun updateNotes(newNotes: List<NoteModel>) {
        notes = newNotes.toMutableList()
        notifyDataSetChanged()
    }

    fun getNoteAt(position: Int): NoteModel {
        return notes[position]
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.note_title_text_view)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.note_description_text_view)
        private val timestampTextView: TextView = itemView.findViewById(R.id.note_timestamp_text_view)

        fun bind(note: NoteModel) {
            titleTextView.text = note.title
            descriptionTextView.text = note.description
            timestampTextView.text = note.getFormattedTimestamp()
        }
    }
}
