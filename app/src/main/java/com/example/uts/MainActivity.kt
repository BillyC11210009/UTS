package com.example.uts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // User not logged in, redirect to login activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val notesList = listOf(
            Note("Note 1", "Content 1"),
            Note("Note 2", "Content 2")
        )
        noteAdapter = NoteAdapter(notesList)
        recyclerView.adapter = noteAdapter
    }
}
