
package com.example.campusconnect

import android.os.Bundle
import androidx.activity.ComponentActivity

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)
        val backToLoginText =
            findViewById<android.widget.TextView>(R.id.backToLoginText)

        //back to login page
        backToLoginText.setOnClickListener {
            finish()
        }
    }
}
