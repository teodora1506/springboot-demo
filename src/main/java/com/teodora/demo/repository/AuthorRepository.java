package com.teodora.demo.repository;

import com.teodora.demo.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    // Dodatne metode po potrebi (npr. findByName) možeš dodati ovde
}