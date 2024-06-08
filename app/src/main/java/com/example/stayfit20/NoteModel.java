package com.example.stayfit20;

import com.google.firebase.Timestamp;

public class NoteModel {
    private String title;
    private String description;
    private Timestamp timestamp;
    private String docId; // Correct the docId field

    // Default constructor
    public NoteModel() {
    }

    // Parameterized constructor
    public NoteModel(String title, String description, Timestamp timestamp, String docId) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.docId = docId;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        // Convert Timestamp to String
        if (timestamp != null) {
            return timestamp.toDate().toString();
        }
        return null;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
