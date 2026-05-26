package com.sop.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {

    @NotBlank(message = "Naam is verplicht")
    private String name;

    @NotBlank(message = "Email is verplicht")
    @Email(message = "Email moet een geldig formaat hebben")
    private String email;

    @NotBlank(message = "Wachtwoord is verplicht")
    @Size(min = 8, message = "Wachtwoord moet minimaal 8 tekens bevatten")
    private String password;

    // Constructors
    public UserDTO() {
    }

    public UserDTO(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}