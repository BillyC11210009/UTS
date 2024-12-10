package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AddNoteActivity : ComponentActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        db = FirebaseFirestore.getInstance()
        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)

        noteId = intent.getStringExtra("noteId")
        noteId?.let { loadNoteData(it) }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            lifecycleScope.launch {
                if (noteId != null) {
                    updateNote()
                } else {
                    saveNote()
                }
            }
        }
    }

    // Load note
    private fun loadNoteData(noteId: String) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).collection("notes").document(noteId)
                .get()
                .addOnSuccessListener { document ->
                    document?.toObject(Note::class.java)?.let { note ->
                        titleEditText.setText(note.title)
                        contentEditText.setText(note.content)
                    }
                }
                .addOnFailureListener {
                    showToast("Failed to load note")
                }
        } ?: showToast("Error: User not authenticated")
    }

    // Save a new note
    private suspend fun saveNote() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            showToast("Title and content cannot be empty")
            return
        }

        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            val newNoteRef = db.collection("users").document(userId).collection("notes").document()
            val note = Note(
                id = newNoteRef.id,
                title = title,
                content = content,
                timestamp = System.currentTimeMillis()
            )

            newNoteRef.set(note)
                .addOnSuccessListener {
                    showToast("Note saved successfully")
                    setResultAndFinish("noteAdded", true)
                }
                .addOnFailureListener {
                    showToast("Failed to save note")
                }
        } ?: showToast("Error: User not authenticated")
    }

    private suspend fun updateNote() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            showToast("Title and content cannot be empty")
            return
        }

        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            noteId?.let { id ->
                val noteRef = db.collection("users").document(userId).collection("notes").document(id)
                val updatedNote = mapOf(
                    "title" to title,
                    "content" to content,
                    "timestamp" to System.currentTimeMillis()
                )

                noteRef.update(updatedNote)
                    .addOnSuccessListener {
                        showToast("Note updated successfully")
                        setResultAndFinish("noteUpdated", true)
                    }
                    .addOnFailureListener {
                        showToast("Failed to update note")
                    }
            }
        } ?: showToast("Error: User not authenticated or note not found")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setResultAndFinish(key: String, value: Boolean) {
        val resultIntent = Intent().apply { putExtra(key, value) }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
