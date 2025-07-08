package com.teodora.demo.service;

import com.teodora.demo.dto.BookResponse;
import com.teodora.demo.dto.BookRequest;

import java.util.List;

public interface BookService {
    List<BookResponse> getAllBooks();
    BookResponse getBookById(Long id);
    BookResponse createBook(BookRequest request);
    BookResponse updateBook(Long id, BookRequest request);
    void deleteBook(Long id);
}