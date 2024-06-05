package com.example.stayfit20

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
    }

    private fun fetchDataAndPopulateRecyclerView() {
        // Fetch data from the database
        notesCollection.get()
            .addOnSuccessListener { result ->
                val notesList = mutableListOf<NoteModel>()
                for (document in result) {
                    val note = document.toObject(NoteModel::class.java)
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
}


