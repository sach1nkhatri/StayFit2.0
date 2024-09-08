package com.example.stayfit20.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.stayfit20.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class Premium : AppCompatActivity() {

    private lateinit var addPaymentPhoto: ImageView
    private lateinit var subscribeBtn: Button
    private lateinit var progressBar: ProgressBar
    private var imageUri: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)

        addPaymentPhoto = findViewById(R.id.addPaymentPhoto)
        subscribeBtn = findViewById(R.id.subscribeBtn)
        progressBar = findViewById(R.id.progressBar)

        val imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                addPaymentPhoto.setImageURI(imageUri)
            }
        }

        addPaymentPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        subscribeBtn.setOnClickListener {
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
            }
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e("FirebaseAuth", "User is not authenticated.")
            return
        }

        val email = user.email
        if (email == null) {
            Log.e("FirebaseAuth", "User email is null.")
            return
        }

        val storageRef = storage.reference.child("payment_slips/${user.uid}/${UUID.randomUUID()}.jpg")

        progressBar.visibility = View.VISIBLE

        storageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    savePaymentInfoToRealtimeDatabase(email, downloadUrl.toString())
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Log.e("FirebaseStorage", "Image upload failed: ${e.message}")
                progressBar.visibility = View.GONE
            }
    }

    private fun savePaymentInfoToRealtimeDatabase(email: String, imageUrl: String) {
        val paymentInfo = hashMapOf(
            "imageUrl" to imageUrl,
            "timestamp" to Calendar.getInstance().time.toString()
        )

        val sanitizedEmail = email.replace(".", ",")
        Log.d("RealtimeDatabase", "Storing data at path: users/$sanitizedEmail/payments")

        db.getReference("users").child(sanitizedEmail).child("payments").push().setValue(paymentInfo)
            .addOnSuccessListener {
                Log.d("RealtimeDatabase", "Data successfully written!")
                progressBar.visibility = View.GONE
                finish()  // Go back to the previous activity
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Log.e("RealtimeDatabase", "Error writing data: ${e.message}")
                progressBar.visibility = View.GONE
            }
    }
}
