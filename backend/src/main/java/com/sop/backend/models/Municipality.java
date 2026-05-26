package com.sop.backend.models;

import jakarta.persistence.Entity;

@Entity
public class Municipality extends BaseEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
