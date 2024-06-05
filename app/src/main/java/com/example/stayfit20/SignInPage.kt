package com.example.stayfit20

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignInPage : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signInButton: Button
    private lateinit var loginBack: TextView // Change to TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_page)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize the views
        nameInput = findViewById(R.id.name_input)
        emailInput = findViewById(R.id.email_input)
        phoneInput = findViewById(R.id.phone_input)
        passwordInput = findViewById(R.id.password_input)
        signInButton = findViewById(R.id.sign_in_btn)
        loginBack = findViewById(R.id.login_back) // Ensure this is a TextView in your XML

        // Set click listener for the sign-in button
        signInButton.setOnClickListener {
            handleSignIn()
        }

        // Set click listener for the login back text view
        loginBack.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun handleSignIn() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (validateInput(name, email, phone, password)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign-in success, update user profile with the name
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(this, "Sign-Up Successful", Toast.LENGTH_SHORT).show()
                                    // Optionally, navigate to another activity
                                    // val intent = Intent(this, AnotherActivity::class.java)
                                    // startActivity(intent)
                                    // finish()
                                } else {
                                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // If sign-up fails, display a message to the user.
                        Toast.makeText(this, "Sign-Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(name: String, email: String, phone: String, password: String): Boolean {
        return name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, login::class.java)
        startActivity(intent)
        finish() // Optional: if you want to close the current activity
    }
}
