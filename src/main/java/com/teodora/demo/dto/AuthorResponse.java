package com.teodora.demo.dto;

import java.util.List;

/**
 * AuthorResponse - DTO za vraćanje informacija o autoru
 * <p>
 * Ova klasa je odličan primer response DTO-a:
 * - Sadrži sve informacije koje klijent treba da vidi
 * - Uključuje povezane podatke (nazivi knjiga) bez full objekta
 * - Immutable pristup - samo getteri, postavljanje se radi preko konstruktora
 * - Odvojena od entiteta za fleksibilnost API-ja
 */
public class AuthorResponse {

    private Long id;
    private String name;
    private List<String> bookTitles; // Samo nazivi knjiga, ne celi objekti

    /**
     * Default konstruktor - potreban za Jackson JSON serijalizaciju
     */
    public AuthorResponse() {}

    /**
     * Glavni konstruktor - koristi se u mapToResponse metodi
     * <p>
     * Ovaj konstruktor prima sve potrebne podatke odjednom,
     * što čini kreiranje objekta sigurnijim i lakšim.
     */
    public AuthorResponse(Long id, String name, List<String> bookTitles) {
        this.id = id;
        this.name = name;
        this.bookTitles = bookTitles;
    }

    /**
     * Za response DTO-ove obično imamo samo getteri:
     * - Jackson koristi ih za JSON serijalizaciju
     * - Čini objekt manje podložnim neočekivanim promenama
     * - API postaje cleaner i predvidljiviji
     */
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getBookTitles() {
        return bookTitles;
    }

    /**
     * Setteri su potrebni za Jackson deserijalizaciju, ali u praksi
     * ne koristimo ih direktno - objekti se kreiraju preko konstruktora
     */
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBookTitles(List<String> bookTitles) {
        this.bookTitles = bookTitles;
    }
} 