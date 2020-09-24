package com.example.datanotespickup;

public class DataModel {
    String noteName;
    String noteUrl;

    public DataModel(String noteName, String noteUrl) {
        this.noteName = noteName;
        this.noteUrl = noteUrl;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteUrl() {
        return noteUrl;
    }

    public void setNoteUrl(String noteUrl) {
        this.noteUrl = noteUrl;
    }
}
