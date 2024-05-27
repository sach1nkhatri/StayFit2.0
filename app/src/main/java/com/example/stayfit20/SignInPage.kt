package com.example.stayfit20

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignInPage : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_page) // Assuming your XML file is named activity_sign_in.xml

        // Initialize the views
        nameInput = findViewById(R.id.name_input)
        emailInput = findViewById(R.id.email_input)
        phoneInput = findViewById(R.id.phone_input)
        passwordInput = findViewById(R.id.password_input)
        signInButton = findViewById(R.id.sign_in_btn)

        // Set click listener for the sign-in button
        signInButton.setOnClickListener {
            handleSignIn()
        }
    }

    private fun handleSignIn() {
        val name = nameInput.text.toString()
        val email = emailInput.text.toString()
        val phone = phoneInput.text.toString()
        val password = passwordInput.text.toString()

        if (validateInput(name, email, phone, password)) {
            // Handle sign-in logic here (e.g., API call, local database, etc.)
            Toast.makeText(this, "Sign-In Successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(name: String, email: String, phone: String, password: String): Boolean {
        return name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()
    }
}
