package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditNoteActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)

        noteId = intent.getStringExtra("noteId")

        noteId?.let { loadNoteData(it) }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            updateNote()
        }

        titleEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                titleEditText.error = "Title cannot be empty"
            }
        }

        contentEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                contentEditText.error = "Content cannot be empty"
            }
        }
    }

    private fun loadNoteData(noteId: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).collection("notes").document(noteId)
                .get()
                .addOnSuccessListener { document ->
                    val note = document.toObject(Note::class.java)
                    note?.let {
                        titleEditText.setText(it.title)
                        contentEditText.setText(it.content)
                    }
                }
                .addOnFailureListener {
                    showToast("Failed to load note")
                }
        } else {
            showToast("Error: User not authenticated")
        }
    }

    private fun updateNote() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            showToast("Title and content cannot be empty")
            return
        }

        val userId = auth.currentUser?.uid
        if (userId != null && noteId != null) {
            val updatedNote = mapOf(
                "title" to title,
                "content" to content,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("users").document(userId).collection("notes").document(noteId!!)
                .update(updatedNote)
                .addOnSuccessListener {
                    showToast("Note updated successfully")
                    setResultAndFinish()
                }
                .addOnFailureListener {
                    showToast("Failed to update note")
                }
        } else {
            showToast("Error: Unable to update note")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setResultAndFinish() {
        setResult(RESULT_OK)
        finish()
    }
}
