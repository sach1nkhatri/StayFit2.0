package com.example.stayfit20.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stayfit20.R
import com.google.firebase.firestore.FirebaseFirestore

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var updateButton: Button

    private val db = FirebaseFirestore.getInstance()
    private var docId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_task)

        titleEditText = findViewById(R.id.titleupdateinput)
        descriptionEditText = findViewById(R.id.descriptionupdateinput)
        updateButton = findViewById(R.id.updatebtn)

        // Retrieve and set the task details from the intent
        titleEditText.setText(intent.getStringExtra("title"))
        descriptionEditText.setText(intent.getStringExtra("description"))
        docId = intent.getStringExtra("docId")

        // Handle update button click
        updateButton.setOnClickListener {
            updateNote()
        }
    }

    private fun updateNote() {
        val updatedTitle = titleEditText.text.toString()
        val updatedDescription = descriptionEditText.text.toString()

        if (updatedTitle.isEmpty() && updatedDescription.isEmpty()) {
            Toast.makeText(this, "Please enter a title or description", Toast.LENGTH_SHORT).show()
        } else {
            val noteUpdates = hashMapOf(
                "title" to updatedTitle,
                "description" to updatedDescription
            )

            docId?.let {
                db.collection("notes").document(it)
                    .update(noteUpdates as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update note", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
