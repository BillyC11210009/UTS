package com.example.uts

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        registerBtn.setOnClickListener {
            val emailStr = email.text.toString()
            val passwordStr = password.text.toString()

            auth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(baseContext, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
