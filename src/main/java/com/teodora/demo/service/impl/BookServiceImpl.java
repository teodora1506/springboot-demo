
package com.teodora.demo.service.impl;

import com.teodora.demo.dto.BookRequest;
import com.teodora.demo.dto.BookResponse;
import com.teodora.demo.model.Author;
import com.teodora.demo.model.Book;
import com.teodora.demo.repository.AuthorRepository;
import com.teodora.demo.repository.BookRepository;
import com.teodora.demo.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        logger.info("BookServiceImpl initialized successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        logger.debug("Starting to retrieve all books from database");
        try {
            List<Book> books = bookRepository.findAll();
            logger.debug("Found {} books in database", books.size());
            
            List<BookResponse> responses = books.stream()
                    .map(BookResponse::fromEntity)
                    .toList();
            
            logger.info("Successfully converted {} books to response DTOs", responses.size());
            return responses;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all books from database", e);
            throw new RuntimeException("Failed to retrieve books", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        logger.debug("Starting to retrieve book with id: {}", id);
        if (id == null) {
            logger.warn("Null ID provided for book retrieval");
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Book not found with ID: {}", id);
                        return new EntityNotFoundException("Book not found with ID: " + id);
                    });
            
            logger.debug("Successfully found book: {}", book.getTitle());
            BookResponse response = BookResponse.fromEntity(book);
            logger.info("Successfully retrieved and converted book with id: {}", id);
            return response;
        } catch (EntityNotFoundException e) {
            logger.warn("Book with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving book with id: {}", id, e);
            throw new RuntimeException("Failed to retrieve book", e);
        }
    }

    @Override
    public BookResponse createBook(BookRequest request) {
        if (request == null) {
            logger.warn("Null BookRequest provided for creation");
            throw new IllegalArgumentException("BookRequest cannot be null");
        }
        logger.debug("Starting to create new book with title: {}", request.getTitle());
        if (request.getAuthorId() == null) {
            logger.warn("Null author ID provided for book creation");
            throw new IllegalArgumentException("Author ID cannot be null");
        }
        
        try {
            // Proveri da li autor postoji
            logger.debug("Looking for author with id: {}", request.getAuthorId());
            Author author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> {
                        logger.warn("Author not found with ID: {} while creating book", request.getAuthorId());
                        return new IllegalArgumentException("Author not found with ID: " + request.getAuthorId());
                    });
            
            logger.debug("Found author: {} for new book", author.getName());

            // Kreiraj novu knjigu
            Book book = new Book();
            book.setTitle(request.getTitle());
            book.setAuthor(author);
            
            logger.debug("Saving new book to database");
            Book savedBook = bookRepository.save(book);
            logger.info("Successfully created book with id: {} and title: '{}'", savedBook.getId(), savedBook.getTitle());
            
            BookResponse response = BookResponse.fromEntity(savedBook);
            logger.debug("Converted saved book to response DTO");
            return response;
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error while creating book: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while creating new book", e);
            throw new RuntimeException("Failed to create book", e);
        }
    }

    @Override
    public BookResponse updateBook(Long id, BookRequest request) {
        logger.debug("Starting to update book with id: {} using new data", id);
        if (id == null) {
            logger.warn("Null ID provided for book update");
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        if (request == null) {
            logger.warn("Null BookRequest provided for update");
            throw new IllegalArgumentException("BookRequest cannot be null");
        }
        if (request.getAuthorId() == null) {
            logger.warn("Null author ID provided for book update");
            throw new IllegalArgumentException("Author ID cannot be null");
        }
        
        try {
            // Pronađi postojeću knjigu
            logger.debug("Looking for existing book with id: {}", id);
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Book not found with ID: {} for update", id);
                        return new EntityNotFoundException("Book not found with ID: " + id);
                    });
            
            String oldTitle = book.getTitle();
            Long oldAuthorId = book.getAuthor() != null ? book.getAuthor().getId() : null;
            logger.debug("Found existing book: '{}' by author ID: {}", oldTitle, oldAuthorId);

            // Pronađi novog autora
            logger.debug("Looking for new author with id: {}", request.getAuthorId());
            Author author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> {
                        logger.warn("Author not found with ID: {} while updating book", request.getAuthorId());
                        return new IllegalArgumentException("Author not found with ID: " + request.getAuthorId());
                    });
            
            logger.debug("Found new author: {}", author.getName());

            // Ažuriraj knjigu
            book.setTitle(request.getTitle());
            book.setAuthor(author);
            
            logger.debug("Saving updated book to database");
            Book updatedBook = bookRepository.save(book);
            logger.info("Successfully updated book with id: {}. Title: '{}' -> '{}', Author ID: {} -> {}", 
                    id, oldTitle, updatedBook.getTitle(), oldAuthorId, updatedBook.getAuthor().getId());
            
            BookResponse response = BookResponse.fromEntity(updatedBook);
            logger.debug("Converted updated book to response DTO");
            return response;
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            logger.warn("Validation error while updating book with id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while updating book with id: {}", id, e);
            throw new RuntimeException("Failed to update book", e);
        }
    }

    @Override
    public void deleteBook(Long id) {
        logger.debug("Starting to delete book with id: {}", id);
        if (id == null) {
            logger.warn("Null ID provided for book deletion");
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        
        try {
            // Proveri da li knjiga postoji
            if (!bookRepository.existsById(id)) {
                logger.warn("Attempted to delete non-existent book with id: {}", id);
                throw new EntityNotFoundException("Book with ID " + id + " not found.");
            }
            
            logger.debug("Book with id: {} exists, proceeding with deletion", id);
            bookRepository.deleteById(id);
            logger.info("Successfully deleted book with id: {}", id);
        } catch (EntityNotFoundException e) {
            logger.warn("Book with id {} not found for deletion", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while deleting book with id: {}", id, e);
            throw new RuntimeException("Failed to delete book", e);
        }
    }
}