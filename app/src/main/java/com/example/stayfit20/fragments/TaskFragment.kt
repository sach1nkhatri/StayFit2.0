package com.example.stayfit20.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stayfit20.R
import com.example.stayfit20.adapter.TaskAdapter
import com.example.stayfit20.model.NoteModel
import com.example.stayfit20.ui.activity.AddTaskActivity
import com.example.stayfit20.ui.activity.UpdateTaskActivity
import com.example.stayfit20.viewmodel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        recyclerView = view.findViewById(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        taskAdapter = TaskAdapter(mutableListOf(), { note ->
            // Handle item click
            val intent = Intent(requireContext(), UpdateTaskActivity::class.java).apply {
                putExtra("title", note.title)
                putExtra("description", note.description)
                putExtra("docId", note.docId)
            }
            startActivity(intent)
        }, { note, direction ->
            if (direction == ItemTouchHelper.LEFT) {
                showDeleteConfirmationDialog(note)
            } else if (direction == ItemTouchHelper.RIGHT) {
                val intent = Intent(requireContext(), UpdateTaskActivity::class.java).apply {
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

        noteViewModel.notes.observe(viewLifecycleOwner, Observer { notes ->
            taskAdapter.updateNotes(notes)
        })

        noteViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            // Show or hide a loading indicator if needed
        })

        noteViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        // Handle FAB click to navigate to AddTaskActivity
        view.findViewById<FloatingActionButton>(R.id.AddTaskBtn).setOnClickListener {
            startActivity(Intent(requireContext(), AddTaskActivity::class.java))
        }

        return view
    }

    private fun showDeleteConfirmationDialog(note: NoteModel) {
        AlertDialog.Builder(requireContext())
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
