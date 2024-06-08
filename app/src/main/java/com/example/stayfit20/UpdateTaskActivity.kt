package com.example.stayfit20

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var updateButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_task)

        titleEditText = findViewById(R.id.titleupdateinput)
        descriptionEditText = findViewById(R.id.descriptionupdateinput)
        updateButton = findViewById(R.id.updatebtn)

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val docId = intent.getStringExtra("docId")

        titleEditText.setText(title)
        descriptionEditText.setText(description)

        updateButton.setOnClickListener {
            val updatedTitle = titleEditText.text.toString()
            val updatedDescription = descriptionEditText.text.toString()

            if (updatedTitle.isEmpty() && updatedDescription.isEmpty()) {
                Toast.makeText(this, "Please enter a title or description", Toast.LENGTH_SHORT).show()
            } else {
                val noteUpdates = hashMapOf(
                    "title" to updatedTitle,
                    "description" to updatedDescription
                )

                db.collection("notes").document(docId!!)
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
