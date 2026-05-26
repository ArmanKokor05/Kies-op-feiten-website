package com.sop.backend.dto;

import java.util.Map;

public class UserAnswerDTO {
    private Map<Long, String> answers;

    public UserAnswerDTO() {
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }
}