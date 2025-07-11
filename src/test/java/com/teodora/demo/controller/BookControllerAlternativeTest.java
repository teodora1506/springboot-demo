package com.teodora.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teodora.demo.dto.BookRequest;
import com.teodora.demo.dto.BookResponse;
import com.teodora.demo.model.Author;
import com.teodora.demo.model.Book;
import com.teodora.demo.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Alternativni pristup testiranju bez @Autowired
 * Koristi manual setup sa mock dependency-jima
 */
@ActiveProfiles("test")
@DisplayName("BookController Alternative Tests (bez @Autowired)")
class BookControllerAlternativeTest {

    private static final Logger logger = LoggerFactory.getLogger(BookControllerAlternativeTest.class);

    private MockMvc mockMvc;
    private BookService bookService;
    private ObjectMapper objectMapper;
    private BookController bookController;

    @BeforeEach
    void setUp() {
        logger.debug("Setting up test environment manually");
        
        // Manual kreiranje dependency-ja
        bookService = mock(BookService.class);
        objectMapper = new ObjectMapper();
        
        // Kreiranje controller-a sa injected dependency-jima
        bookController = new BookController(bookService);
        
        // Setup MockMvc sa standalone setup
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .build();
        
        logger.debug("Manual test setup completed");
    }

    @Test
    @DisplayName("Manual Test - GET /books should call service")
    void getAllBooks_ShouldCallService() throws Exception {
        logger.info("Testing manual setup - GET /books endpoint");
        
        // Given
        when(bookService.getAllBooks()).thenReturn(List.of());
        
        // When & Then
        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(bookService).getAllBooks();
        logger.info("Manual test passed successfully");
    }

    @Test
    @DisplayName("Manual Test - POST /books should call service")
    void createBook_ShouldCallService() throws Exception {
        logger.info("Testing manual setup - POST /books endpoint");
        
        // Given
        BookRequest request = new BookRequest("Test Book", 1L);
        String requestJson = objectMapper.writeValueAsString(request);
        
        // Mock return value
        BookResponse mockResponse = new BookResponse(1L, "Test Book", 1L, "Test Author");
        when(bookService.createBook(any(BookRequest.class))).thenReturn(mockResponse);
        
        // When & Then
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Book"));
        
        verify(bookService).createBook(any(BookRequest.class));
        logger.info("Manual create test passed successfully");
    }
} 