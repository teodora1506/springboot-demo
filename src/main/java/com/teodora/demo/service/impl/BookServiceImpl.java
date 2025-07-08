
package com.teodora.demo.service.impl;

import com.teodora.demo.dto.BookRequest;
import com.teodora.demo.dto.BookResponse;
import com.teodora.demo.model.Author;
import com.teodora.demo.model.Book;
import com.teodora.demo.repository.AuthorRepository;
import com.teodora.demo.repository.BookRepository;
import com.teodora.demo.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::fromEntity)
                .toList();
    }

    @Override
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + id));
        return BookResponse.fromEntity(book);
    }

    @Override
    public BookResponse createBook(BookRequest request) {
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(author);

        return BookResponse.fromEntity(bookRepository.save(book));
    }

    @Override
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        book.setTitle(request.getTitle());
        book.setAuthor(author);

        return BookResponse.fromEntity(bookRepository.save(book));
    }
    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book with ID " + id + " not found.");
        }

        bookRepository.deleteById(id);
    }
