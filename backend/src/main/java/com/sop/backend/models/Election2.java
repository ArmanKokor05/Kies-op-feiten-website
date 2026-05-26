package com.sop.backend.models;

import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Election2 extends BaseEntity {
    private String identifier;
    private String name;
    private LocalDate date;
    private String category;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
