package com.sop.backend.dto;

public record CandidateDTO(Long id,
                           String firstName,
                           String lastName,
                           String initials,
                           String gender,
                           String qualifyingAdress) {
}
