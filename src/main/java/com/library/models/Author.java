package com.library.models;

public class Author {
    private int id;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String biography;

    public Author() {}

    public Author(int id, String firstName, String lastName, String birthDate, String biography) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.biography = biography;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s %s | Дата рождения: %s",
                id, firstName, lastName, birthDate != null ? birthDate : "Не указана");
    }
}