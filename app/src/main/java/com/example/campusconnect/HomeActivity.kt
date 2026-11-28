package com.example.campusconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.mutableStateOf

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // References Firebase Authentication and Firestore
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        // Retrieves the currently logged-in user's unique ID
        val userId = auth.currentUser?.uid

        // Stores the user's name and updates the UI when it changes
        val userName = mutableStateOf("User")

        // Retrieves the user's profile from Firestore
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    // Updates the user's name after retrieving their profile
                    userName.value = document.getString("name") ?: "User"
                }
        }

        setContent {
            //Handles user logout
            HomeScreen(userName = userName.value, onLogout = {
                // Signs out the user and returns to the login screen
                FirebaseAuth.getInstance().signOut()

                // Returns the user to the login page
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                // Prevents returning to the home screen using the back button
                finish()
            })
        }
    }
}