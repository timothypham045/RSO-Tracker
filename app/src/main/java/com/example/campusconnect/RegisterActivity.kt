
package com.example.campusconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.EditText
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //displays register screen
        setContentView(R.layout.activity_register)
        val auth = FirebaseAuth.getInstance()


        //XML fields
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailInput = findViewById<EditText>(R.id.registerEmailInput)
        val passwordInput = findViewById<EditText>(R.id.registerPasswordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordInput)

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()


            //Checks for errors
            if (password != confirmPassword) {
                confirmPasswordInput.error = "Passwords do not match"
                return@setOnClickListener
            }

            if (name.isBlank()) {
                nameInput.error = "Name cannot be blank"
                return@setOnClickListener
            }

            if (email.isBlank()) {
                emailInput.error = "Email cannot be blank"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Please enter a valid email"
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordInput.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            // Sends email and password to Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        val backToLoginText =
            findViewById<android.widget.TextView>(R.id.backToLoginText)

        //back to login page
        backToLoginText.setOnClickListener {
            finish()
        }
    }
}
