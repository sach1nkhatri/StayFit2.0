package com.example.stayfit20.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stayfit20.R
import com.example.stayfit20.adapter.TaskAdapter
import com.example.stayfit20.model.NoteModel
import com.example.stayfit20.viewmodel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_view)

        recyclerView = findViewById(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter(mutableListOf(), { note ->
            // Handle item click
            val intent = Intent(this, UpdateTaskActivity::class.java).apply {
                putExtra("title", note.title)
                putExtra("description", note.description)
                putExtra("docId", note.docId)
            }
            startActivity(intent)
        }, { note, direction ->
            if (direction == ItemTouchHelper.LEFT) {
                showDeleteConfirmationDialog(note)
            } else if (direction == ItemTouchHelper.RIGHT) {
                val intent = Intent(this, UpdateTaskActivity::class.java).apply {
                    putExtra("title", note.title)
                    putExtra("description", note.description)
                    putExtra("docId", note.docId)
                }
                startActivity(intent)
            }
        })
        recyclerView.adapter = taskAdapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = taskAdapter.getNoteAt(position)
                taskAdapter.onItemSwiped(note, direction)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        noteViewModel.notes.observe(this, Observer { notes ->
            taskAdapter.updateNotes(notes)
        })

        noteViewModel.isLoading.observe(this, Observer { isLoading ->
            // Show or hide a loading indicator if needed
        })

        noteViewModel.error.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        // Handle FAB click to navigate to AddTaskActivity
        findViewById<FloatingActionButton>(R.id.AddTaskBtn).setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
    }

    private fun showDeleteConfirmationDialog(note: NoteModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { dialog, _ ->
                noteViewModel.deleteNote(note)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                taskAdapter.updateNotes(noteViewModel.notes.value ?: emptyList())
                dialog.dismiss()
            }
            .show()
    }
}
