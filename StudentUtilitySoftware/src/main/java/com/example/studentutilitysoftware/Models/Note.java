package com.example.studentutilitysoftware.Models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Note {

    private final StringProperty noteID;
    private final StringProperty title;
    private final StringProperty content;
    private final IntegerProperty priority;
    private final ObjectProperty<LocalDate> date;

    public Note(String noteID, String title, String content, int priority, LocalDate date) {
        this.noteID = new SimpleStringProperty(noteID);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.priority = new SimpleIntegerProperty(priority);
        this.date = new SimpleObjectProperty<>(date);
    }

    public String getNoteID() {
        return noteID.get();
    }

    public void setNoteID(String noteID) {
        this.noteID.set(noteID);
    }

    public StringProperty noteIDProperty() {
        return noteID;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public StringProperty contentProperty() {
        return content;
    }

    public int getPriority() {
        return priority.get();
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    public IntegerProperty priorityProperty() {
        return priority;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }
}
