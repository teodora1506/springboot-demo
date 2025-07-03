package com.teodora.demo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * AuthorRequest - DTO za kreiranje/ažuriranje autora
 * <p>
 * Ova klasa predstavlja najbolju praksu za input DTO-ove:
 * - Sadrži samo polja koja korisnik može da pošalje
 * - Koristi Bean Validation anotacije za validaciju
 * - Odvojena je od entiteta za bolje separation of concerns
 * - Čini API sigurnijim - korisnik ne može poslati polja koja ne treba
 */
public class AuthorRequest {

    /**
     * Ime autora - obavezno polje
     * 
     * @NotBlank anotacija:
     * - Proverava da string nije null, prazan ili samo whitespace
     * - Automatski će baciti validation error ako nije ispunjeno
     * - message parametar definiše poruku greške
     */
    @NotBlank(message = "Name must not be blank")
    private String name;

    /**
     * Default konstruktor - potreban za Jackson JSON deserijalizaciju
     */
    public AuthorRequest() {}

    /**
     * Konstruktor sa parametrima - za lakše kreiranje objekta u testovima
     */
    public AuthorRequest(String name) {
        this.name = name;
    }

    /**
     * Getteri i setteri su potrebni jer:
     * - Jackson koristi ih za JSON deserijalizaciju
     * - Spring Boot automatski poziva setteri kad prima JSON
     * - Validacija se izvršava nakon deserijalizacije
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
} 