package com.example.studentutilitysoftware.Models;

import javafx.beans.property.*;

import java.sql.Date;

public class Expense {

    private IntegerProperty id;
    private StringProperty title;
    private ObjectProperty<Date> date;
    private FloatProperty amount;

    public Expense() {
        this.id = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.date = new SimpleObjectProperty<>();
        this.amount = new SimpleFloatProperty();
    }

    public Expense(int id, String title, Date date, float amount) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.date = new SimpleObjectProperty<>(date);
        this.amount = new SimpleFloatProperty(amount);
    }

    // Getters and setters for JavaFX properties
    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getId() {
        return id.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getTitle() {
        return title.get();
    }

    public ObjectProperty<Date> dateProperty() {
        return date;
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    public Date getDate() {
        return date.get();
    }

    public FloatProperty amountProperty() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount.set(amount);
    }

    public float getAmount() {
        return amount.get();
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id.get() +
                ", title='" + title.get() + '\'' +
                ", date=" + date.get() +
                ", amount=" + amount.get() +
                '}';
    }

}
