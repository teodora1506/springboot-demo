package com.teodora.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookRequest {
    
    @NotBlank(message = "Naziv knjige je obavezan")
    private String title;
    
    @NotNull(message = "ID autora je obavezan")
    private Long authorId;

    public BookRequest() {}

    public BookRequest(String title, Long authorId) {
        this.title = title;
        this.authorId = authorId;
    }
    public String getTitle() {
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    public Long getAuthorId() { 
        return authorId; 
    }
    
    public void setAuthorId(Long authorId) { 
        this.authorId = authorId; 
    }
} 