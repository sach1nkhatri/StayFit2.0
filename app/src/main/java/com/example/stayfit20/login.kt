package com.example.stayfit20

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class login : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var signIn: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            // If user is logged in, navigate to Dashboard
            navigateToDashboard()
        }

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        signIn = findViewById(R.id.SignInBtn)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.i("Login", "signInWithEmail:success")
                            navigateToDashboard()
                        } else {
                            Log.w("Login", "signInWithEmail:failure", task.exception)
                            Toast.makeText(this@login, "Login Failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter username and password.", Toast.LENGTH_SHORT).show()
            }
        }

        signIn.setOnClickListener {
            val intent = Intent(this@login, SignInPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this@login, Dashboard::class.java)
        startActivity(intent)
        finish()
    }
}
