package com.example.stayfit20.model

import com.google.firebase.Timestamp

data class NoteModel(
    var title: String = "",
    var description: String = "",
    var timestamp: Timestamp? = null,
    var docId: String = "",
    var userId: String = "" // Add this line
) {
    // Convert Timestamp to String
    fun getFormattedTimestamp(): String? {
        return timestamp?.toDate()?.toString()
    }
}
