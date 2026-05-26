package com.sop.backend.dto;

import java.util.Map;

public class SaveSurveyResultDTO {
    private Map<Long, String> answers;

    public SaveSurveyResultDTO() {
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }
}