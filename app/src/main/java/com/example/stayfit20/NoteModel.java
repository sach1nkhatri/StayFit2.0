package com.example.stayfit20;

import com.google.firebase.Timestamp;

public class NoteModel {
    String title;
    String description;
    Timestamp timestamp;



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

    public CharSequence getTimestamp() {
        // Convert Timestamp to CharSequence (String)
        return timestamp.toString();
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public NoteModel() {
    }

    public boolean getDocId() {
        return false;
    }
}
