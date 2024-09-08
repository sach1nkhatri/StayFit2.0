package com.example.stayfit20.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.stayfit20.R
import com.example.stayfit20.ui.activity.AboutUs
import com.example.stayfit20.ui.activity.Premium
import com.example.stayfit20.ui.activity.login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream
import java.net.URL

class SettingsFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userReference = database.reference.child("users")
        storage = FirebaseStorage.getInstance()

        // Initialize views
        userNameTextView = view.findViewById(R.id.textViewName)
        emailTextView = view.findViewById(R.id.TextViewEmail)
        phoneTextView = view.findViewById(R.id.phoneNumber)
        logoutButton = view.findViewById(R.id.LogOut)
        PremiumBtn = view.findViewById(R.id.Premium)
        aboutUsBtn = view.findViewById(R.id.AboutUs)
        imageViewUser = view.findViewById(R.id.imageViewUser)
        progressBar = view.findViewById(R.id.progressBar)
        userDataProgressBar = view.findViewById(R.id.userDataProgressBar)

        // Set click listeners
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        PremiumBtn.setOnClickListener {
            val intent = Intent(requireContext(), Premium::class.java)
            startActivity(intent)
        }

        aboutUsBtn.setOnClickListener {
            val intent = Intent(requireContext(), AboutUs::class.java)
            startActivity(intent)
        }

        // Set click listener for selecting an image
        imageViewUser.setOnClickListener {
            selectImage()
        }

        return view
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
            Log.e("SettingsFragment", "Email is null")
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
                        "SettingsFragment",
                        "Data loaded: Name=$name, Email=$email, Phone=$phone, ImageUrl=$imageUrl"
                    )
                } else {
                    userDataProgressBar.visibility = View.GONE
                    Log.e("SettingsFragment", "User data does not exist for email: $email")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                userDataProgressBar.visibility = View.GONE
                Log.e("SettingsFragment", "Failed to read user data: ${error.message}")
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
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
        val intent = Intent(requireContext(), login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
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

        storageRef.putFile(imageUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                userReference.child(emailKey).child("image_url").setValue(imageUrl).addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Log.i("SettingsFragment", "Image URL saved to database")
                        DownloadImageTask(imageViewUser, progressBar).execute(imageUrl)
                    } else {
                        Log.e("SettingsFragment", "Failed to save image URL: ${task.exception?.message}")
                    }
                }
            }
        }.addOnFailureListener { exception ->
            progressBar.visibility = View.GONE
            Log.e("SettingsFragment", "Image upload failed: ${exception.message}")
        }
    }

    private class DownloadImageTask(
        private val imageView: ImageView,
        private val progressBar: ProgressBar
    ) : AsyncTask<String, Void, Bitmap?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg urls: String): Bitmap? {
            val url = urls[0]
            return try {
                val inputStream: InputStream = URL(url).openStream()
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                Log.e("DownloadImageTask", "Error downloading image: ${e.message}")
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            progressBar.visibility = View.GONE
            result?.let {
                imageView.setImageBitmap(it)
            }
        }
    }
}
