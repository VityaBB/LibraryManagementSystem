package com.library.models;

public class Book {
    private int id;
    private String title;
    private String isbn;
    private int publicationYear;
    private int publisherId;
    private int totalCopies;
    private int pageCount;
    private String description;

    public Book() {}

    public Book(int id, String title, String isbn, int publicationYear, 
                int publisherId, int totalCopies, 
                int pageCount, String description) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.publisherId = publisherId;
        this.totalCopies = totalCopies;
        this.pageCount = pageCount;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    public int getPublisherId() { return publisherId; }
    public void setPublisherId(int publisherId) { this.publisherId = publisherId; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | %s | %d г. | %d экз. | %d стр.",
                id, title, isbn, publicationYear, totalCopies, pageCount);
    }
}