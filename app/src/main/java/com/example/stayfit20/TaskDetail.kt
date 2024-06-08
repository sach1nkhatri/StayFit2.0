package com.example.stayfit20

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class TaskDetail : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_detail)

        titleEditText = findViewById(R.id.titleinput)
        descriptionEditText = findViewById(R.id.descriptioninput)
        saveButton = findViewById(R.id.savebtn)

        saveButton.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()

        if (title.isEmpty() && description.isEmpty()) {
            Toast.makeText(this, "Please enter a title or description", Toast.LENGTH_SHORT).show()
        } else {
            val note = hashMapOf(
                "title" to title,
                "description" to description,
                "timestamp" to Timestamp.now()
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
