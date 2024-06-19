package com.example.stayfit20.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.stayfit20.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)

        titleEditText = findViewById(R.id.titleinput)
        descriptionEditText = findViewById(R.id.descriptioninput)
        saveButton = findViewById(R.id.savebtn)

        // Handle save button click
        saveButton.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val userId = auth.currentUser?.uid

        if (title.isEmpty() && description.isEmpty()) {
            Toast.makeText(this, "Please enter a title or description", Toast.LENGTH_SHORT).show()
        } else {
            val note = hashMapOf(
                "title" to title,
                "description" to description,
                "timestamp" to Timestamp.now(),
                "userId" to userId
            )

            db.collection("notes")
                .add(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
