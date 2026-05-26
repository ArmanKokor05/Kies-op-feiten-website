package com.sop.backend.dto;

import java.time.LocalDateTime;

public class ForumPostDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private Long parentId;

    // Constructors
    public ForumPostDTO() {}

    // Voor nieuwe posts (zonder ID en createdAt)
    public ForumPostDTO(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // Voor bestaande posts (met ID en createdAt)
    public ForumPostDTO(Long id, String title, String content, String author, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}