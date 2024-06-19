package com.example.stayfit20.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stayfit20.model.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NoteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var notesListener: ListenerRegistration? = null

    private val _notes = MutableLiveData<List<NoteModel>>()
    val notes: LiveData<List<NoteModel>> = _notes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        fetchNotes()
    }

    private fun fetchNotes() {
        val userId = auth.currentUser?.uid ?: return
        _isLoading.value = true
        notesListener = db.collection("notes")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _error.value = "Listen failed: ${e.message}"
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val notesList = mutableListOf<NoteModel>()
                    for (document in snapshot.documents) {
                        val note = document.toObject(NoteModel::class.java)
                        if (note != null) {
                            note.docId = document.id
                            notesList.add(note)
                        }
                    }
                    _notes.value = notesList
                    _isLoading.value = false
                } else {
                    _notes.value = emptyList()
                    _isLoading.value = false
                }
            }
    }

    fun deleteNote(note: NoteModel) {
        db.collection("notes").document(note.docId)
            .delete()
            .addOnSuccessListener {
                // Note successfully deleted
            }
            .addOnFailureListener { e ->
                _error.value = "Delete failed: ${e.message}"
            }
    }

    override fun onCleared() {
        super.onCleared()
        notesListener?.remove()
    }
}
