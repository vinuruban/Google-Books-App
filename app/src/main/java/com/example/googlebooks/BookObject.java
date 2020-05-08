package com.example.googlebooks;

import android.graphics.Bitmap;

public class BookObject {

    private Bitmap thumbnail;
    private String title;
    private String description;
    private String authors;
    private String publishedDate;

    public BookObject(Bitmap thumbnail, String title, String description, String authors, String publishedDate) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.description = description;
        this.authors = authors;
        this.publishedDate = publishedDate;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublishedDate() {
        return publishedDate;
    }
}
