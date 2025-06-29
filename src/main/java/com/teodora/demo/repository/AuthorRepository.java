package com.teodora.demo.repository;

import com.teodora.demo.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AuthorRepository - Data Access Layer za Author entitet
 * <p>
 * Ovo je odličan primer Spring Data JPA repository pattern-a:
 * - Nasledjuje JpaRepository<Author, Long> što znači:
 *   - Author je tip entiteta koji repository rukuje
 *   - Long je tip primary key-a (ID polja)
 * - Spring automatski generiše implementaciju za osnovne CRUD operacije
 * - @Repository anotacija označava da je ovo Data Access Object (DAO)
 * - Ne moraš pisati SQL upite za osnovne operacije!
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    /**
     * Ovde možeš dodati dodatne metode po potrebi
     * <p>
     * Spring Data JPA omogućava kreiranje metoda po konvenciji:
     * - findByName(String name) - automatski generiše SQL WHERE name = ?
     * - findByNameContaining(String name) - generiše WHERE name LIKE %?%
     * - findByBooksTitle(String title) - join preko books relacije
     */
}