package com.teodora.demo.repository;

import com.teodora.demo.model.Author;
import com.teodora.demo.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BookRepository Tests")
class BookRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(BookRepositoryTest.class);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Author testAuthor;
    private Book testBook;

    @BeforeEach
    void setUp() {
        logger.debug("Setting up test data for repository tests");
        
        // Kreiraj test autora
        testAuthor = new Author("Test Author");
        testAuthor = entityManager.persistAndFlush(testAuthor);
        logger.debug("Created test author with id: {}", testAuthor.getId());
        
        // Kreiraj test knjigu
        testBook = new Book("Test Book", testAuthor);
        testBook = entityManager.persistAndFlush(testBook);
        logger.debug("Created test book with id: {}", testBook.getId());
        
        // Očisti EntityManager cache
        entityManager.clear();
    }

    @Test
    @DisplayName("findAll - Should return all books")
    void findAll_ShouldReturnAllBooks() {
        logger.info("Testing BookRepository.findAll()");
        
        // Dodaj još jednu knjigu
        Book secondBook = new Book("Second Book", testAuthor);
        entityManager.persistAndFlush(secondBook);
        entityManager.clear();
        
        // When
        List<Book> books = bookRepository.findAll();
        
        // Then
        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Test Book", "Second Book");
        
        logger.info("findAll test passed - found {} books", books.size());
    }

    @Test
    @DisplayName("findById - Should return book when found")
    void findById_ExistingId_ShouldReturnBook() {
        logger.info("Testing BookRepository.findById() with existing ID: {}", testBook.getId());
        
        // When
        Optional<Book> found = bookRepository.findById(testBook.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Book");
        assertThat(found.get().getAuthor().getName()).isEqualTo("Test Author");
        
        logger.info("findById test passed - book found: {}", found.get().getTitle());
    }

    @Test
    @DisplayName("findById - Should return empty when not found")
    void findById_NonExistingId_ShouldReturnEmpty() {
        Long nonExistentId = 999L;
        logger.info("Testing BookRepository.findById() with non-existent ID: {}", nonExistentId);
        
        // When
        Optional<Book> found = bookRepository.findById(nonExistentId);
        
        // Then
        assertThat(found).isEmpty();
        
        logger.info("findById with non-existent ID test passed");
    }

    @Test
    @DisplayName("save - Should save new book")
    void save_NewBook_ShouldPersistBook() {
        logger.info("Testing BookRepository.save() with new book");
        
        // Given
        Book newBook = new Book("New Book Title", testAuthor);
        
        // When
        Book savedBook = bookRepository.save(newBook);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("New Book Title");
        assertThat(savedBook.getAuthor().getId()).isEqualTo(testAuthor.getId());
        
        // Proveri da li je stvarno sačuvano u bazi
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("New Book Title");
        
        logger.info("save new book test passed - saved book with ID: {}", savedBook.getId());
    }

    @Test
    @DisplayName("save - Should update existing book")
    void save_ExistingBook_ShouldUpdateBook() {
        logger.info("Testing BookRepository.save() with existing book update");
        
        // Given
        Book existingBook = bookRepository.findById(testBook.getId()).orElseThrow();
        String newTitle = "Updated Book Title";
        existingBook.setTitle(newTitle);
        
        // When
        Book updatedBook = bookRepository.save(existingBook);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        assertThat(updatedBook.getId()).isEqualTo(testBook.getId());
        assertThat(updatedBook.getTitle()).isEqualTo(newTitle);
        
        // Proveri da li je stvarno ažurirano u bazi
        Optional<Book> foundBook = bookRepository.findById(testBook.getId());
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo(newTitle);
        
        logger.info("save existing book test passed - updated title to: {}", newTitle);
    }

    @Test
    @DisplayName("existsById - Should return true for existing book")
    void existsById_ExistingId_ShouldReturnTrue() {
        logger.info("Testing BookRepository.existsById() with existing ID: {}", testBook.getId());
        
        // When
        boolean exists = bookRepository.existsById(testBook.getId());
        
        // Then
        assertThat(exists).isTrue();
        
        logger.info("existsById test passed - book exists");
    }

    @Test
    @DisplayName("existsById - Should return false for non-existing book")
    void existsById_NonExistingId_ShouldReturnFalse() {
        Long nonExistentId = 999L;
        logger.info("Testing BookRepository.existsById() with non-existent ID: {}", nonExistentId);
        
        // When
        boolean exists = bookRepository.existsById(nonExistentId);
        
        // Then
        assertThat(exists).isFalse();
        
        logger.info("existsById with non-existent ID test passed");
    }

    @Test
    @DisplayName("deleteById - Should delete existing book")
    void deleteById_ExistingId_ShouldDeleteBook() {
        logger.info("Testing BookRepository.deleteById() with existing ID: {}", testBook.getId());
        
        // Given
        Long bookId = testBook.getId();
        assertThat(bookRepository.existsById(bookId)).isTrue();
        
        // When
        bookRepository.deleteById(bookId);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        assertThat(bookRepository.existsById(bookId)).isFalse();
        Optional<Book> foundBook = bookRepository.findById(bookId);
        assertThat(foundBook).isEmpty();
        
        logger.info("deleteById test passed - book deleted");
    }

    @Test
    @DisplayName("deleteById - Should not throw exception for non-existing book")
    void deleteById_NonExistingId_ShouldNotThrowException() {
        Long nonExistentId = 999L;
        logger.info("Testing BookRepository.deleteById() with non-existent ID: {}", nonExistentId);
        
        // When & Then (should not throw exception)
        assertThatCode(() -> {
            bookRepository.deleteById(nonExistentId);
            entityManager.flush();
        }).doesNotThrowAnyException();
        
        logger.info("deleteById with non-existent ID test passed");
    }

    @Test
    @DisplayName("count - Should return correct number of books")
    void count_ShouldReturnCorrectCount() {
        logger.info("Testing BookRepository.count()");
        
        // Initially we have 1 book
        assertThat(bookRepository.count()).isEqualTo(1);
        
        // Add another book
        Book secondBook = new Book("Second Book", testAuthor);
        entityManager.persistAndFlush(secondBook);
        
        // Now we should have 2 books
        assertThat(bookRepository.count()).isEqualTo(2);
        
        logger.info("count test passed - correct count returned");
    }

    @Test
    @DisplayName("Author-Book relationship - Should be properly maintained")
    void authorBookRelationship_ShouldBeMaintained() {
        logger.info("Testing Author-Book relationship integrity");
        
        // When
        Book foundBook = bookRepository.findById(testBook.getId()).orElseThrow();
        
        // Then
        assertThat(foundBook.getAuthor()).isNotNull();
        assertThat(foundBook.getAuthor().getId()).isEqualTo(testAuthor.getId());
        assertThat(foundBook.getAuthor().getName()).isEqualTo("Test Author");
        
        logger.info("Author-Book relationship test passed");
    }
} 