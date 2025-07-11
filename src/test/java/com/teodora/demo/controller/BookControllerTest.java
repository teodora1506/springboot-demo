package com.teodora.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teodora.demo.dto.BookRequest;
import com.teodora.demo.dto.BookResponse;
import com.teodora.demo.model.Author;
import com.teodora.demo.model.Book;
import com.teodora.demo.repository.AuthorRepository;
import com.teodora.demo.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("BookController Integration Tests")
class BookControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(BookControllerTest.class);

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Author testAuthor;
    private Book testBook;

    @BeforeEach
    void setUp() {
        logger.debug("Setting up test environment");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Očisti bazu pre svakog testa
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        
        // Kreiraj test podatke
        testAuthor = new Author("Test Author");
        testAuthor = authorRepository.save(testAuthor);
        logger.debug("Created test author with id: {}", testAuthor.getId());
        
        testBook = new Book("Test Book", testAuthor);
        testBook = bookRepository.save(testBook);
        logger.debug("Created test book with id: {}", testBook.getId());
    }

    @Test
    @DisplayName("GET /books - Should return all books")
    void getAllBooks_ShouldReturnAllBooks() throws Exception {
        logger.info("Testing GET /books endpoint");
        
        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testBook.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is("Test Book")))
                .andExpect(jsonPath("$[0].authorId", is(testAuthor.getId().intValue())))
                .andExpect(jsonPath("$[0].authorName", is("Test Author")));
        
        logger.info("GET /books test passed successfully");
    }

    @Test
    @DisplayName("GET /books/{id} - Should return book by ID")
    void getBookById_ValidId_ShouldReturnBook() throws Exception {
        logger.info("Testing GET /books/{} endpoint", testBook.getId());
        
        mockMvc.perform(get("/books/{id}", testBook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBook.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Test Book")))
                .andExpect(jsonPath("$.authorId", is(testAuthor.getId().intValue())))
                .andExpect(jsonPath("$.authorName", is("Test Author")));
        
        logger.info("GET /books/{} test passed successfully", testBook.getId());
    }

    @Test
    @DisplayName("GET /books/{id} - Should return 404 for non-existent ID")
    void getBookById_NonExistentId_ShouldReturn404() throws Exception {
        Long nonExistentId = 999L;
        logger.info("Testing GET /books/{} with non-existent ID", nonExistentId);
        
        mockMvc.perform(get("/books/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        
        logger.info("GET /books/{} with non-existent ID test passed", nonExistentId);
    }

    @Test
    @DisplayName("POST /books - Should create new book")
    void createBook_ValidRequest_ShouldCreateBook() throws Exception {
        logger.info("Testing POST /books endpoint");
        
        BookRequest request = new BookRequest("New Book", testAuthor.getId());
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("New Book")))
                .andExpect(jsonPath("$.authorId", is(testAuthor.getId().intValue())))
                .andExpect(jsonPath("$.authorName", is("Test Author")));
        
        logger.info("POST /books test passed successfully");
    }

    @Test
    @DisplayName("POST /books - Should return 400 for non-existent author")
    void createBook_NonExistentAuthor_ShouldReturn400() throws Exception {
        Long nonExistentAuthorId = 999L;
        logger.info("Testing POST /books with non-existent author ID: {}", nonExistentAuthorId);
        
        BookRequest request = new BookRequest("New Book", nonExistentAuthorId);
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
        
        logger.info("POST /books with non-existent author test passed");
    }

    @Test
    @DisplayName("POST /books - Should return 400 for missing title")
    void createBook_MissingTitle_ShouldReturn400() throws Exception {
        logger.info("Testing POST /books with missing title");
        
        BookRequest request = new BookRequest("", testAuthor.getId()); // Empty title
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
        
        logger.info("POST /books with missing title test passed");
    }

    @Test
    @DisplayName("PUT /books/{id} - Should update existing book")
    void updateBook_ValidRequest_ShouldUpdateBook() throws Exception {
        logger.info("Testing PUT /books/{} endpoint", testBook.getId());
        
        BookRequest request = new BookRequest("Updated Book Title", testAuthor.getId());
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(put("/books/{id}", testBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBook.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Updated Book Title")))
                .andExpect(jsonPath("$.authorId", is(testAuthor.getId().intValue())))
                .andExpect(jsonPath("$.authorName", is("Test Author")));
        
        logger.info("PUT /books/{} test passed successfully", testBook.getId());
    }

    @Test
    @DisplayName("PUT /books/{id} - Should return 404 for non-existent book")
    void updateBook_NonExistentId_ShouldReturn404() throws Exception {
        Long nonExistentId = 999L;
        logger.info("Testing PUT /books/{} with non-existent ID", nonExistentId);
        
        BookRequest request = new BookRequest("Updated Title", testAuthor.getId());
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(put("/books/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isNotFound());
        
        logger.info("PUT /books/{} with non-existent ID test passed", nonExistentId);
    }

    @Test
    @DisplayName("DELETE /books/{id} - Should delete existing book")
    void deleteBook_ValidId_ShouldDeleteBook() throws Exception {
        logger.info("Testing DELETE /books/{} endpoint", testBook.getId());
        
        mockMvc.perform(delete("/books/{id}", testBook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        
        // Proveri da li je knjiga obrisana
        mockMvc.perform(get("/books/{id}", testBook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        logger.info("DELETE /books/{} test passed successfully", testBook.getId());
    }

    @Test
    @DisplayName("DELETE /books/{id} - Should return 404 for non-existent book")
    void deleteBook_NonExistentId_ShouldReturn404() throws Exception {
        Long nonExistentId = 999L;
        logger.info("Testing DELETE /books/{} with non-existent ID", nonExistentId);
        
        mockMvc.perform(delete("/books/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        
        logger.info("DELETE /books/{} with non-existent ID test passed", nonExistentId);
    }
} 