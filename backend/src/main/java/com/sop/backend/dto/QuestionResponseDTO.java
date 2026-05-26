package com.sop.backend.dto;

public record QuestionResponseDTO(
        String title,
        String question,
        String sourceUrl,
        String sourceName,
        Long id
) {
}