package com.example.stayfit20

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class settings_activity : AppCompatActivity() {

    private lateinit var logoutButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings) // Ensure this matches your XML layout file name
        enableEdgeToEdge()
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize the logout button
        logoutButton = findViewById(R.id.LogOut)

        // Set click listener for the logout button
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
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
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
        startActivity(intent)
        finish() // Close the current activity
    }
}
