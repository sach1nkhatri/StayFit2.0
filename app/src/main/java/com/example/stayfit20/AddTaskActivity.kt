package com.example.stayfit20

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
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

        // Add ItemTouchHelper for swipe-to-delete functionality
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = adapter.getNoteAtPosition(position)
                adapter.deleteItem(position)
                deleteFromDatabase(note.docId)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                // Optionally add some background drawing logic here (e.g., draw a delete icon)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun fetchDataAndPopulateRecyclerView() {
        notesCollection.get()
            .addOnSuccessListener { result ->
                val notesList = mutableListOf<NoteModel>()
                for (document in result) {
                    val note = document.toObject(NoteModel::class.java)
                    note.docId = document.id  // Set the document ID
                    notesList.add(note)
                }
                adapter = TaskAdaptor(notesList)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                // For example, you can log the error or show a toast message
            }
    }

    private fun deleteFromDatabase(docId: String) {
        notesCollection.document(docId).delete()
            .addOnSuccessListener {
                // Note successfully deleted from the database
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                // For example, you can log the error or show a toast message
            }
    }
}
