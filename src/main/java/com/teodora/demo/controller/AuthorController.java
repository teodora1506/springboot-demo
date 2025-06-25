package com.teodora.demo.controller;


import com.teodora.demo.model.Author;
import com.teodora.demo.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;

    // GET /authors - dobavi sve autore
    @GetMapping
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    // GET /authors/{id} - dobavi autora po ID-ju
    @GetMapping("/{id}")
    public Author getAuthorById(@PathVariable Long id) {
        return authorRepository.findById(id).orElse(null);
    }

    // POST /authors - dodaj novog autora
    @PostMapping
    public Author createAuthor(@RequestBody Author author) {
        return authorRepository.save(author);
    }

    // PUT /authors/{id} - ažuriraj postojećeg autora
    @PutMapping("/{id}")
    public Author updateAuthor(@PathVariable Long id, @RequestBody Author updatedAuthor) {
        Optional<Author> optionalAuthor = authorRepository.findById(id);
        if (optionalAuthor.isPresent()) {
            Author author = optionalAuthor.get();
            author.setName(updatedAuthor.getName());
            return authorRepository.save(author);
        } else {
            return null;
        }
    }

    // DELETE /authors/{id} - obriši autora po ID-ju
    @DeleteMapping("/{id}")
    public void deleteAuthor(@PathVariable Long id) {
        authorRepository.deleteById(id);
    }
}
