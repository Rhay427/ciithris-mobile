package com.example.capstone_employee.ui.dashboard;

public class Announcement {
    private String title,author,description,datestamp;

    public Announcement() {
    }

    public Announcement(String title, String author, String description, String datestamp) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.datestamp = datestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }
}
