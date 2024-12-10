package com.example.uts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.AlertDialog

class NotesAdapter(private var notesList: List<Note>) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.noteTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.noteContent)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesList[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content

        holder.editButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditNoteActivity::class.java)
            intent.putExtra("noteId", note.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle("Delete Note")
                .setMessage("Do you want to delete this note?")
                .setPositiveButton("Yes") { dialog, _ ->
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        FirebaseFirestore.getInstance().collection("users")
                            .document(userId)
                            .collection("notes")
                            .document(note.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Note deleted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                (notesList as MutableList).removeAt(position)
                                notifyItemRemoved(position)
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Failed to delete note",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun submitList(newNotes: List<Note>) {
        notesList = newNotes
        notifyDataSetChanged()
    }
}
