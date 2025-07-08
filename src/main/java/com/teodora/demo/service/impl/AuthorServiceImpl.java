package com.teodora.demo.service.impl;



import com.teodora.demo.dto.AuthorRequest;
import com.teodora.demo.dto.AuthorResponse;
import com.teodora.demo.model.Author;
import com.teodora.demo.repository.AuthorRepository;
import com.teodora.demo.service.AuthorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with ID: " + id));
        return mapToResponse(author);
    }

    @Override
    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = new Author();
        author.setName(request.getName());
        return mapToResponse(authorRepository.save(author));
    }

    @Override
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with ID: " + id));
        author.setName(request.getName());
        return mapToResponse(authorRepository.save(author));
    }

    @Override
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Author not found with ID: " + id);
        }
        authorRepository.deleteById(id);
    }

    private AuthorResponse mapToResponse(Author author) {
        List<String> bookTitles = author.getBooks() != null ?
                author.getBooks().stream()
                        .map(book -> book.getTitle())
                        .collect(Collectors.toList())
                : List.of();

        return new AuthorResponse(author.getId(), author.getName(), bookTitles);
    }
}