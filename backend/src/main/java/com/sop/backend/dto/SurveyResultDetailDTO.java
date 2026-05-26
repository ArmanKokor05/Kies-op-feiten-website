package com.sop.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyResultDetailDTO {
    private Long id;
    private LocalDateTime completedAt;
    private List<MatchResultDTO> matches;

    public SurveyResultDetailDTO() {
    }

    public SurveyResultDetailDTO(Long id, LocalDateTime completedAt, List<MatchResultDTO> matches) {
        this.id = id;
        this.completedAt = completedAt;
        this.matches = matches;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<MatchResultDTO> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchResultDTO> matches) {
        this.matches = matches;
    }
}