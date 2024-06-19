package com.example.stayfit20.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.stayfit20.R
import com.example.stayfit20.ui.activity.login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class Settings_activity : AppCompatActivity() {


    private lateinit var logoutButton: Button
    private lateinit var userNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userReference: DatabaseReference
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userReference = database.reference.child("users")

        // Initialize views
        userNameTextView = findViewById(R.id.textViewName)
        emailTextView = findViewById(R.id.TextViewEmail)
        phoneTextView = findViewById(R.id.phoneNumber)
        logoutButton = findViewById(R.id.LogOut)

        // Set click listener for the logout button
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
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

        val emailKey = email.replace(".", ",")
        userReference.child(emailKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val phone = snapshot.child("phone").getValue(String::class.java)

                    // Update UI with fetched data
                    userNameTextView.text = name
                    emailTextView.text = email
                    phoneTextView.text = phone

                    Log.i(
                        "Settings_activity",
                        "Data loaded: Name=$name, Email=$email, Phone=$phone"
                    )
                } else {
                    Log.e("Settings_activity", "User data does not exist for email: $email")
                }
            }

            override fun onCancelled(error: DatabaseError) {
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
}
