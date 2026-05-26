package com.sop.backend.dto;

import java.time.LocalDateTime;

public class SurveyResultResponseDTO {
    private Long id;
    private String topMatchParty;
    private Double topMatchPercentage;
    private LocalDateTime completedAt;

    public SurveyResultResponseDTO() {
    }

    public SurveyResultResponseDTO(Long id, String topMatchParty,
                                   Double topMatchPercentage, LocalDateTime completedAt) {
        this.id = id;
        this.topMatchParty = topMatchParty;
        this.topMatchPercentage = topMatchPercentage;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopMatchParty() {
        return topMatchParty;
    }

    public void setTopMatchParty(String topMatchParty) {
        this.topMatchParty = topMatchParty;
    }

    public Double getTopMatchPercentage() {
        return topMatchPercentage;
    }

    public void setTopMatchPercentage(Double topMatchPercentage) {
        this.topMatchPercentage = topMatchPercentage;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}