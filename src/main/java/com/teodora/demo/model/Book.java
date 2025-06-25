package com.teodora.demo.model;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // Veza sa autorom – više knjiga može imati jednog autora
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    // --- Konstruktori ---
    public Book() {}

    public Book(String title, Author author) {
        this.title = title;
        this.author = author;
    }

    // --- Getteri i setteri ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
