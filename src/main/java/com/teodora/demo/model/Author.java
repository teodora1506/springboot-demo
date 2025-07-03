package com.teodora.demo.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Author - JPA entitet za autore
 * <p>
 * Ova klasa je odličan primer JPA entiteta:
 * - @Entity označava da je to baza podataka tabela
 * - Sadrži JPA anotacije za mapiranje u bazu
 * - Definiše relacije sa drugim entitetima (knjige)
 * - Koristi najbolje prakse za JPA entitete
 */
@Entity
public class Author {

    /**
     * Primarni ključ tabele
     * <p>
     * Ove anotacije rade sledeće:
     * - @Id označava da je ovo primary key
     * - @GeneratedValue govori da baza automatski generiše vrednost
     * - IDENTITY strategija koristi AUTO_INCREMENT u bazi
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ime autora - osnovno polje u tabeli
     */
    private String name;

    /**
     * Veza sa Book entitetom - jedan autor ima više knjiga
     * <p>
     * Ova JPA relacija je ključna:
     * - @OneToMany označava 1:N vezu (1 autor : N knjiga)
     * - mappedBy="author" govori da Book entitet "owns" vezu preko author polja
     * - cascade=CascadeType.ALL znači da operacije na autoru se prenose na knjige
     * - orphanRemoval=true znači da se knjige brišu ako se uklone iz liste
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books;

    /**
     * Default konstruktor - OBAVEZAN za JPA entitete!
     * <p>
     * JPA zahteva prazan konstruktor za kreiranje objekta iz baze.
     */
    public Author() {}

    /**
     * Konstruktor sa parametrima - za lakše kreiranje u kodu
     */
    public Author(String name) {
        this.name = name;
    }

    /**
     * Za JPA entitete UVEK pišemo getteri i setteri:
     * - JPA koristi ih za pristup poljima
     * - Omogućavaju lazy loading i proxy objekte
     * - Potrebni su za property-based access
     */
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter za knjige - možda neće uvek biti učitane zbog lazy loading-a
     * <p>
     * Kada pristupaš books listi, JPA može automatski učitati
     * knjige iz baze ako nisu već učitane (lazy loading).
     */
    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
