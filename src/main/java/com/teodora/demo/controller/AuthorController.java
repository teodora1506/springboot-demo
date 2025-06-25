package com.teodora.demo.controller;

import com.teodora.demo.model.Author;
import com.teodora.demo.repository.AuthorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // GET /authors - vrati sve autore
    @GetMapping
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    // GET /authors/{id} - vrati autora po ID
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        Optional<Author> author = authorRepository.findById(id);
        return author.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /authors - napravi novog autora
    @PostMapping
    public Author createAuthor(@RequestBody Author author) {
        return authorRepository.save(author);
    }

    // PUT /authors/{id} - izmeni autora
    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @RequestBody Author updatedAuthor) {
        return authorRepository.findById(id).map(author -> {
            author.setName(updatedAuthor.getName());
            author.setBooks(updatedAuthor.getBooks()); // ako imaš veze sa knjigama
            Author savedAuthor = authorRepository.save(author);
            return ResponseEntity.ok(savedAuthor);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE /authors/{id} - obriši autora
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
