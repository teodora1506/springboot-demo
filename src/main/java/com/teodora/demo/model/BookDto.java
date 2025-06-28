package com.teodora.demo.model;

public record BookDto(Long id, String title, Long authorId, String authorName) {

    public static BookDto toDto(com.teodora.demo.model.Book b) {
        return new BookDto(
                b.getId(),
                b.getTitle(),
                b.getAuthor().getId(),
                b.getAuthor().getName()
        );
    }
}


