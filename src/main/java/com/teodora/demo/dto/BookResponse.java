package com.teodora.demo.dto;

import com.teodora.demo.model.Book;

public record BookResponse(Long id, String title, Long authorId, String authorName) {

    public static BookResponse fromEntity(Book book) {
        // Uvek proveravaj null vrednosti kad radiš sa vezanim objektima!
        Long authorId = book.getAuthor() != null ? book.getAuthor().getId() : null;
        String authorName = book.getAuthor() != null ? book.getAuthor().getName() : "Nepoznat autor";

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                authorId,
                authorName
        );
    }
} 