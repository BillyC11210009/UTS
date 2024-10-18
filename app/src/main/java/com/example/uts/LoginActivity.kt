package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        loginBtn.setOnClickListener {
            val emailStr = email.text.toString()
            val passwordStr = password.text.toString()

            auth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, NotesActivity::class.java))
                    } else {
                        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Link to registration activity
        findViewById<Button>(R.id.registerLink).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
