package com.teodora.demo.controller;

import com.teodora.demo.dto.AuthorRequest;
import com.teodora.demo.dto.AuthorResponse;
import com.teodora.demo.model.Author;
import com.teodora.demo.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AuthorController - REST API za upravljanje autorima
 * <p>
 * Ovaj controller je odličan primer najboljih praksi:
 * - Koristi DTO pattern (AuthorRequest/AuthorResponse) za čist API
 * - ResponseEntity za pravilno HTTP response handling
 * - Validacija sa @Valid anotacijom
 * - Proper error handling sa HTTP status kodovima
 * - Separation of concerns - controller samo rukuje HTTP layer-om
 */
@RestController
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;

    /**
     * GET /authors - Dohvata sve autore sa njihovim knjigama
     * <p>
     * Ovde koristimo:
     * - Stream API za elegantnu transformaciju podataka
     * - ResponseEntity.ok() za HTTP 200 status
     * - DTO pattern za bezbednost API-ja
     */
    @GetMapping
    public ResponseEntity<List<AuthorResponse>> getAllAuthors() {
        List<AuthorResponse> response = authorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /authors/{id} - Dohvata jednog autora po ID-ju
     * <p>
     * Ovaj pristup je odličan jer:
     * - Koristi Optional za null-safe operacije
     * - Vraća 404 ako autor ne postoji
     * - Koristi method chaining za čitljiviji kod
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        return authorRepository.findById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /authors - Kreira novog autora
     * <p>
     * Kod kreiranja:
     * - @Valid aktivira validaciju na AuthorRequest
     * - Vraćamo 201 Created status za novo kreiran resurs
     * - Koristimo DTO pattern za input i output
     */
    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest request) {
        Author author = new Author();
        author.setName(request.getName());
        Author saved = authorRepository.save(author);
        return ResponseEntity.status(201).body(mapToResponse(saved)); // 201 Created
    }

    /**
     * PUT /authors/{id} - Ažurira postojećeg autora
     * <p>
     * Update pattern:
     * - Prvo proveravamo da li autor postoji
     * - Ažuriramo postojeći entitet (ne kreiramo novi)
     * - Vraćamo 404 ako autor ne postoji
     * - Koristimo clear variable names
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorRequest request) {
        Optional<Author> authorOpt = authorRepository.findById(id);
        if (authorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Author author = authorOpt.get();
        author.setName(request.getName());
        Author updatedAuthor = authorRepository.save(author);
        return ResponseEntity.ok(mapToResponse(updatedAuthor));
    }

    /**
     * DELETE /authors/{id} - Briše autora
     * <p>
     * Kod brisanja:
     * - Prvo proveravamo da li autor postoji
     * - 204 No Content za uspešno brisanje
     * - 404 Not Found ako autor ne postoji
     * - JPA automatski briše povezane knjige zbog orphanRemoval=true
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (!authorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        authorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper metoda za mapiranje entiteta u response DTO
     * <p>
     * Ovakve helper metode su važne jer:
     * - Centralizuju logiku transformacije
     * - Čine kod čitljivijim i lakšim za održavanje
     * - Omogućavaju lako dodavanje novih polja u budućnosti
     * - Null-safe pristup sa defensive programming
     */
    private AuthorResponse mapToResponse(Author author) {
        List<String> bookTitles = author.getBooks() != null ?
                author.getBooks().stream()
                        .map(book -> book.getTitle())
                        .collect(Collectors.toList())
                : List.of(); // Prazan list ako nema knjiga

        return new AuthorResponse(author.getId(), author.getName(), bookTitles);
    }
}
