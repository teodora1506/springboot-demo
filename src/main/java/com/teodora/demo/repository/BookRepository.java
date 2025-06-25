package com.teodora.demo.repository;

import com.teodora.demo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Dodatne metode po potrebi (npr. findByTitle) možeš dodati ovde
}
