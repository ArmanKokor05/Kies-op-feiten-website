package com.sop.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
@Entity

@Table(name = "Questions")
public class Question extends BaseEntity{

    private String title;
    private String question;

    public String getTitle() {return title;}

    public void setTitle(String Title) {
        if (Title == null || Title.isEmpty()) {
            throw new IllegalArgumentException("Title Cannot Be Empty or null");
        }
        this.title = Title;
    }

    public String getQuestion() {return question;}

    public void setQuestion(String Question) {
        if (Question == null || Question.isEmpty()) {
            throw new IllegalArgumentException("Question Cannot Be Empty or null");
        }
        this.question = Question;
    }
    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "source_name", length = 255)
    private String sourceName;

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
