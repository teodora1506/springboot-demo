package com.teodora.demo.controller;

import com.teodora.demo.dto.BookRequest;
import com.teodora.demo.dto.BookResponse;
import com.teodora.demo.service.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    // Konstruktor-based dependency injection
    public BookController(BookService bookService) {
        this.bookService = bookService;
        logger.info("BookController initialized successfully");
    }

    /**
     * GET /books - Vrati sve knjige
     */
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        logger.debug("Received request to get all books");
        try {
            List<BookResponse> books = bookService.getAllBooks();
            logger.info("Successfully retrieved {} books", books.size());
            logger.debug("Books retrieved: {}", books);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all books", e);
            throw e;
        }
    }

    /**
     * GET /books/{id} - Vrati knjigu po ID-ju
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        logger.debug("Received request to get book with id: {}", id);
        if (id == null || id <= 0) {
            logger.warn("Invalid book ID provided: {}", id);
            throw new IllegalArgumentException("Book ID must be positive number");
        }
        
        try {
            BookResponse book = bookService.getBookById(id);
            logger.info("Successfully retrieved book with id: {}", id);
            logger.debug("Book details: {}", book);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving book with id: {}", id, e);
            throw e;
        }
    }

    /**
     * POST /books - Kreiraj novu knjigu
     */
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        logger.debug("Received request to create new book: {}", request);
        if (request == null) {
            logger.warn("Null book request received");
            throw new IllegalArgumentException("Book request cannot be null");
        }
        
        try {
            BookResponse createdBook = bookService.createBook(request);
            logger.info("Successfully created new book with id: {}", createdBook.id());
            logger.debug("Created book details: {}", createdBook);
            return ResponseEntity.status(201).body(createdBook);
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error while creating book: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while creating new book", e);
            throw e;
        }
    }

    /**
     * PUT /books/{id} - Ažuriraj postojeću knjigu
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id,
                                                   @Valid @RequestBody BookRequest request) {
        logger.debug("Received request to update book with id: {} using data: {}", id, request);
        if (id == null || id <= 0) {
            logger.warn("Invalid book ID provided for update: {}", id);
            throw new IllegalArgumentException("Book ID must be positive number");
        }
        if (request == null) {
            logger.warn("Null book request received for update");
            throw new IllegalArgumentException("Book request cannot be null");
        }
        
        try {
            BookResponse updatedBook = bookService.updateBook(id, request);
            logger.info("Successfully updated book with id: {}", id);
            logger.debug("Updated book details: {}", updatedBook);
            return ResponseEntity.ok(updatedBook);
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error while updating book with id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while updating book with id: {}", id, e);
            throw e;
        }
    }

    /**
     * DELETE /books/{id} - Obrisi knjigu
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        logger.debug("Received request to delete book with id: {}", id);
        if (id == null || id <= 0) {
            logger.warn("Invalid book ID provided for deletion: {}", id);
            throw new IllegalArgumentException("Book ID must be positive number");
        }
        
        try {
            bookService.deleteBook(id);
            logger.info("Successfully deleted book with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error occurred while deleting book with id: {}", id, e);
            throw e;
        }
    }
}
