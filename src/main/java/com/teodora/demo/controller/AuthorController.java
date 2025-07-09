package com.teodora.demo.controller;

import com.teodora.demo.dto.AuthorRequest;
import com.teodora.demo.dto.AuthorResponse;
import com.teodora.demo.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    // Konstruktor-based dependency injection (bolje od @Autowired)
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * GET /authors - Vraća sve autore
     */
    @GetMapping
    public ResponseEntity<List<AuthorResponse>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    /**
     * GET /authors/{id} - Vraća jednog autora po ID-ju
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    /**
     * POST /authors - Kreira novog autora
     */
    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse created = authorService.createAuthor(request);
        return ResponseEntity.status(201).body(created); // 201 Created
    }

    /**
     * PUT /authors/{id} - Ažurira postojećeg autora
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable Long id,
                                                       @Valid @RequestBody AuthorRequest request) {
        AuthorResponse updated = authorService.updateAuthor(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /authors/{id} - Briše autora
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}