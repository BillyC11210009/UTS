package com.example.uts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class NotesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private lateinit var notesList: List<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        auth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dummy data
        notesList = listOf(
            Note("Title 1", "Content 1"),
            Note("Title 2", "Content 2"),
            Note("Title 3", "Content 3")
        )

        adapter = NoteAdapter(notesList)
        recyclerView.adapter = adapter
    }
}
