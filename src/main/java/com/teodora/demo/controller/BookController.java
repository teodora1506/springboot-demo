package com.teodora.demo.controller;

import com.teodora.demo.dto.BookRequest;
import com.teodora.demo.dto.BookResponse;
import com.teodora.demo.model.Book;
import com.teodora.demo.repository.BookRepository;
import com.teodora.demo.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = bookRepository.findAll()
                .stream()
                .map(BookResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(BookResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        // Prvo proveravamo da li autor postoji
        Optional<com.teodora.demo.model.Author> authorOpt = authorRepository.findById(request.getAuthorId());
        if (authorOpt.isEmpty()) {
            return ResponseEntity.badRequest().build(); // 400 - autor ne postoji
        }

        // Kreiramo novu knjigu
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(authorOpt.get());

        Book savedBook = bookRepository.save(book);
        return ResponseEntity.status(201).body(BookResponse.fromEntity(savedBook)); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if (bookOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 - knjiga ne postoji
        }

        // Proveravamo da li novi autor postoji
        Optional<com.teodora.demo.model.Author> authorOpt = authorRepository.findById(request.getAuthorId());
        if (authorOpt.isEmpty()) {
            return ResponseEntity.badRequest().build(); // 400 - autor ne postoji
        }

        // Ažuriramo knjigu
        Book book = bookOpt.get();
        book.setTitle(request.getTitle());
        book.setAuthor(authorOpt.get());

        Book updatedBook = bookRepository.save(book);
        return ResponseEntity.ok(BookResponse.fromEntity(updatedBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // 404 - knjiga ne postoji
        }

        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 - uspešno obrisano
    }
}
