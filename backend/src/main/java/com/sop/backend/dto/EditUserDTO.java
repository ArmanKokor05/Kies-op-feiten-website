package com.sop.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class EditUserDTO {
    @NotBlank(message = "Name can not be empty")
    private String name;

    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}