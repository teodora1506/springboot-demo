package com.teodora.demo.service;

import com.teodora.demo.dto.BookRequest;
import com.teodora.demo.dto.BookResponse;
import com.teodora.demo.model.Author;
import com.teodora.demo.model.Book;
import com.teodora.demo.repository.AuthorRepository;
import com.teodora.demo.repository.BookRepository;
import com.teodora.demo.service.impl.BookServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookServiceImpl Unit Tests")
class BookServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImplTest.class);

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Author testAuthor;
    private Book testBook;
    private BookRequest testBookRequest;

    @BeforeEach
    void setUp() {
        logger.debug("Setting up test data");
        testAuthor = new Author("Test Author");
        testAuthor.setId(1L);
        
        testBook = new Book("Test Book", testAuthor);
        testBook.setId(1L);
        
        testBookRequest = new BookRequest("New Book", 1L);
        logger.debug("Test data setup completed");
    }

    @Test
    @DisplayName("getAllBooks - Should return all books")
    void getAllBooks_ShouldReturnAllBooks() {
        logger.info("Testing getAllBooks method");
        
        // Given
        Book book2 = new Book("Second Book", testAuthor);
        book2.setId(2L);
        List<Book> books = Arrays.asList(testBook, book2);
        
        when(bookRepository.findAll()).thenReturn(books);
        
        // When
        List<BookResponse> result = bookService.getAllBooks();
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Test Book");
        assertThat(result.get(1).title()).isEqualTo("Second Book");
        
        verify(bookRepository).findAll();
        logger.info("getAllBooks test passed successfully");
    }

    @Test
    @DisplayName("getAllBooks - Should handle empty repository")
    void getAllBooks_EmptyRepository_ShouldReturnEmptyList() {
        logger.info("Testing getAllBooks with empty repository");
        
        // Given
        when(bookRepository.findAll()).thenReturn(Arrays.asList());
        
        // When
        List<BookResponse> result = bookService.getAllBooks();
        
        // Then
        assertThat(result).isEmpty();
        verify(bookRepository).findAll();
        logger.info("getAllBooks empty repository test passed");
    }

    @Test
    @DisplayName("getBookById - Should return book when found")
    void getBookById_ValidId_ShouldReturnBook() {
        logger.info("Testing getBookById with valid ID");
        
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        
        // When
        BookResponse result = bookService.getBookById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Test Book");
        assertThat(result.authorId()).isEqualTo(1L);
        assertThat(result.authorName()).isEqualTo("Test Author");
        
        verify(bookRepository).findById(1L);
        logger.info("getBookById valid ID test passed");
    }

    @Test
    @DisplayName("getBookById - Should throw exception when not found")
    void getBookById_NonExistentId_ShouldThrowException() {
        logger.info("Testing getBookById with non-existent ID");
        
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> bookService.getBookById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book not found with ID: 999");
        
        verify(bookRepository).findById(999L);
        logger.info("getBookById non-existent ID test passed");
    }

    @Test
    @DisplayName("getBookById - Should throw exception for null ID")
    void getBookById_NullId_ShouldThrowException() {
        logger.info("Testing getBookById with null ID");
        
        // When & Then
        assertThatThrownBy(() -> bookService.getBookById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book ID cannot be null");
        
        verify(bookRepository, never()).findById(any());
        logger.info("getBookById null ID test passed");
    }

    @Test
    @DisplayName("createBook - Should create book successfully")
    void createBook_ValidRequest_ShouldCreateBook() {
        logger.info("Testing createBook with valid request");
        
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        
        // When
        BookResponse result = bookService.createBook(testBookRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Test Book");
        assertThat(result.authorId()).isEqualTo(1L);
        
        verify(authorRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
        logger.info("createBook valid request test passed");
    }

    @Test
    @DisplayName("createBook - Should throw exception for non-existent author")
    void createBook_NonExistentAuthor_ShouldThrowException() {
        logger.info("Testing createBook with non-existent author");
        
        // Given
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());
        BookRequest request = new BookRequest("Test Book", 999L);
        
        // When & Then
        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Author not found with ID: 999");
        
        verify(authorRepository).findById(999L);
        verify(bookRepository, never()).save(any());
        logger.info("createBook non-existent author test passed");
    }

    @Test
    @DisplayName("createBook - Should throw exception for null request")
    void createBook_NullRequest_ShouldThrowException() {
        logger.info("Testing createBook with null request");
        
        // When & Then
        assertThatThrownBy(() -> bookService.createBook(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("BookRequest cannot be null");
        
        verify(authorRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
        logger.info("createBook null request test passed");
    }

    @Test
    @DisplayName("updateBook - Should update book successfully")
    void updateBook_ValidRequest_ShouldUpdateBook() {
        logger.info("Testing updateBook with valid request");
        
        // Given
        Book updatedBook = new Book("Updated Book", testAuthor);
        updatedBook.setId(1L);
        
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        
        BookRequest updateRequest = new BookRequest("Updated Book", 1L);
        
        // When
        BookResponse result = bookService.updateBook(1L, updateRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Updated Book");
        
        verify(bookRepository).findById(1L);
        verify(authorRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
        logger.info("updateBook valid request test passed");
    }

    @Test
    @DisplayName("updateBook - Should throw exception for non-existent book")
    void updateBook_NonExistentBook_ShouldThrowException() {
        logger.info("Testing updateBook with non-existent book");
        
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> bookService.updateBook(999L, testBookRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book not found with ID: 999");
        
        verify(bookRepository).findById(999L);
        verify(bookRepository, never()).save(any());
        logger.info("updateBook non-existent book test passed");
    }

    @Test
    @DisplayName("deleteBook - Should delete book successfully")
    void deleteBook_ValidId_ShouldDeleteBook() {
        logger.info("Testing deleteBook with valid ID");
        
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);
        
        // When
        bookService.deleteBook(1L);
        
        // Then
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
        logger.info("deleteBook valid ID test passed");
    }

    @Test
    @DisplayName("deleteBook - Should throw exception for non-existent book")
    void deleteBook_NonExistentBook_ShouldThrowException() {
        logger.info("Testing deleteBook with non-existent book");
        
        // Given
        when(bookRepository.existsById(999L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> bookService.deleteBook(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with ID 999 not found");
        
        verify(bookRepository).existsById(999L);
        verify(bookRepository, never()).deleteById(any());
        logger.info("deleteBook non-existent book test passed");
    }

    @Test
    @DisplayName("deleteBook - Should throw exception for null ID")
    void deleteBook_NullId_ShouldThrowException() {
        logger.info("Testing deleteBook with null ID");
        
        // When & Then
        assertThatThrownBy(() -> bookService.deleteBook(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book ID cannot be null");
        
        verify(bookRepository, never()).existsById(any());
        verify(bookRepository, never()).deleteById(any());
        logger.info("deleteBook null ID test passed");
    }
} 