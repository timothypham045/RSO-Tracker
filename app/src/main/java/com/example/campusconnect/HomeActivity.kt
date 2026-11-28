package com.example.campusconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //Handles user logout
            HomeScreen(onLogout = {
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