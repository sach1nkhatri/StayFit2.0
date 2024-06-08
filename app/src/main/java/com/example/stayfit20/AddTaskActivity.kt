package com.example.stayfit20

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class AddTaskActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addNoteBtn: FloatingActionButton
    private lateinit var adapter: TaskAdaptor
    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        enableEdgeToEdge()

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the FloatingActionButton
        addNoteBtn = findViewById(R.id.AddTaskBtn)

        // Set onClickListener for the FloatingActionButton
        addNoteBtn.setOnClickListener {
            // Create an Intent to start the TaskDetailActivity
            val intent = Intent(this@AddTaskActivity, TaskDetail::class.java)
            startActivity(intent)
        }

        // Fetch data from the database and populate the RecyclerView
        fetchDataAndPopulateRecyclerView()

        // Apply padding to the main layout to account for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Attach ItemTouchHelper to RecyclerView for swipe actions
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        // Refresh the data when returning to this activity
        fetchDataAndPopulateRecyclerView()
    }

    private fun fetchDataAndPopulateRecyclerView() {
        // Fetch data from the database
        notesCollection.get()
            .addOnSuccessListener { result ->
                val notesList = mutableListOf<NoteModel>()
                for (document in result) {
                    val note = document.toObject(NoteModel::class.java)
                    note.docId = document.id
                    notesList.add(note)
                }
                // Populate the RecyclerView with fetched data
                adapter = TaskAdaptor(notesList)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                // For example, you can log the error or show a toast message
            }
    }

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val note = adapter.getNoteAtPosition(position)

            if (direction == ItemTouchHelper.LEFT) {
                // Show confirmation dialog before deletion
                AlertDialog.Builder(this@AddTaskActivity)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                        // Delete the note
                        db.collection("notes").document(note.docId).delete()
                            .addOnSuccessListener {
                                adapter.removeNoteAtPosition(position)
                            }
                    }
                    .setNegativeButton(android.R.string.no) { dialog, which ->
                        // Cancel the swipe
                        adapter.notifyItemChanged(position)
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            } else if (direction == ItemTouchHelper.RIGHT) {
                // Edit the note
                val intent = Intent(this@AddTaskActivity, UpdateTaskActivity::class.java).apply {
                    putExtra("title", note.title)
                    putExtra("description", note.description)
                    putExtra("docId", note.docId)
                }
                startActivity(intent)
                adapter.notifyItemChanged(position) // Reset the swiped item
            }
        }
    }
}
