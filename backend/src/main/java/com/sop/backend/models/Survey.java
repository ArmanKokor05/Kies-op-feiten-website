package com.sop.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

// This is a entity that connects to the "Survey" table in the database.
// It represents a real survey, with a title, description, and list of questions.

@Entity
@Table(name = "Survey")
public class Survey extends BaseEntity {

    private String title;
    private String description;
    private LocalDate createdDate;

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
}
