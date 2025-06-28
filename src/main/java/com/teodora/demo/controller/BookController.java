package com.teodora.demo.controller;

import com.teodora.demo.model.Book;
import com.teodora.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.teodora.demo.model.BookDto;

import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    // GET /books - dobavi sve knjige
    @GetMapping
    public java.util.List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream().map(BookDto::toDto).toList();
    }

    // GET /books/{id} - dobavi knjigu po ID-ju
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    // POST /books - dodaj novu knjigu
    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    // PUT /books/{id} - ažuriraj postojeću knjigu
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            return bookRepository.save(book);
        } else {
            return null;
        }
    }

    // DELETE /books/{id} - obriši knjigu po ID-ju
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
    }
}
