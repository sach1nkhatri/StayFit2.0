package com.example.stayfit20.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.stayfit20.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream
import java.net.URL

class Settings_activity : AppCompatActivity() {

    private lateinit var logoutButton: Button
    private lateinit var PremiumBtn: TextView
    private lateinit var aboutUsBtn: TextView
    private lateinit var userNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var imageViewUser: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var userDataProgressBar: ProgressBar

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userReference: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private var currentUser: FirebaseUser? = null

    // Activity result launcher for image selection
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                // Upload image to Firebase Storage
                uploadImageToStorage(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userReference = database.reference.child("users")
        storage = FirebaseStorage.getInstance()

        // Initialize views
        userNameTextView = findViewById(R.id.textViewName)
        emailTextView = findViewById(R.id.TextViewEmail)
        phoneTextView = findViewById(R.id.phoneNumber)
        logoutButton = findViewById(R.id.LogOut)
        PremiumBtn = findViewById(R.id.Premium)
        aboutUsBtn = findViewById(R.id.AboutUs)
        imageViewUser = findViewById(R.id.imageViewUser)
        progressBar = findViewById(R.id.progressBar)
        userDataProgressBar = findViewById(R.id.userDataProgressBar)

        // Set click listeners
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        PremiumBtn.setOnClickListener {
            val intent = Intent(this@Settings_activity, Premium::class.java)
            startActivity(intent)
        }

        aboutUsBtn.setOnClickListener {
            val intent = Intent(this@Settings_activity, AboutUs::class.java)
            startActivity(intent)
        }

        // Set click listener for selecting an image
        imageViewUser.setOnClickListener {
            selectImage()
        }
    }

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser
        if (currentUser != null) {
            loadUserData(currentUser!!.email)
        } else {
            navigateToLogin()
        }
    }

    private fun loadUserData(email: String?) {
        if (email == null) {
            Log.e("Settings_activity", "Email is null")
            return
        }

        // Show progress bar while loading data
        userDataProgressBar.visibility = View.VISIBLE

        val emailKey = email.replace(".", ",")
        userReference.child(emailKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val phone = snapshot.child("phone").getValue(String::class.java)
                    val imageUrl = snapshot.child("image_url").getValue(String::class.java)

                    // Update UI with fetched data
                    userNameTextView.text = name
                    emailTextView.text = email
                    phoneTextView.text = phone

                    if (!imageUrl.isNullOrEmpty()) {
                        // Download and set image using AsyncTask
                        DownloadImageTask(imageViewUser, userDataProgressBar).execute(imageUrl)
                    } else {
                        userDataProgressBar.visibility = View.GONE
                    }

                    Log.i(
                        "Settings_activity",
                        "Data loaded: Name=$name, Email=$email, Phone=$phone, ImageUrl=$imageUrl"
                    )
                } else {
                    userDataProgressBar.visibility = View.GONE
                    Log.e("Settings_activity", "User data does not exist for email: $email")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                userDataProgressBar.visibility = View.GONE
                Log.e("Settings_activity", "Failed to read user data: ${error.message}")
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { dialog, which ->
            auth.signOut()
            navigateToLogin()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getContent.launch(intent)
    }

    private fun uploadImageToStorage(imageUri: Uri) {
        val emailKey = currentUser?.email?.replace(".", ",") ?: return
        val storageRef = storage.reference.child("user_images").child("$emailKey.jpg")
        progressBar.visibility = View.VISIBLE

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully, get download URL
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveImageUrlToDatabase(uri.toString())
                    progressBar.visibility = View.GONE
                    imageViewUser.setImageURI(imageUri) // Display selected image
                }.addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    Log.e("Settings_activity", "Failed to get download URL: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Log.e("Settings_activity", "Failed to upload image: ${e.message}")
            }
    }

    private fun saveImageUrlToDatabase(imageUrl: String) {
        val emailKey = currentUser?.email?.replace(".", ",") ?: return
        userReference.child(emailKey).child("image_url").setValue(imageUrl)
            .addOnSuccessListener {
                Log.i("Settings_activity", "Image URL saved to database: $imageUrl")
            }
            .addOnFailureListener { e ->
                Log.e("Settings_activity", "Failed to save image URL: ${e.message}")
            }
    }

    private class DownloadImageTask(private val imageView: ImageView, private val progressBar: ProgressBar) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val urlDisplay = urls[0]
            var bitmap: Bitmap? = null
            try {
                val inputStream: InputStream = URL(urlDisplay).openStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                Log.e("Settings_activity", "Error downloading image: ${e.message}")
            }
            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            progressBar.visibility = View.GONE
            if (result != null) {
                imageView.setImageBitmap(result)
            }
        }
    }
}
