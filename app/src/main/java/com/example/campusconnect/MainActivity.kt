
package com.example.campusconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val signupText = findViewById<android.widget.TextView>(R.id.signupText)

        signupText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
