package com.sop.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "survey_results")
public class SurveyResult extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = true)
    private Survey survey;

    @Column(columnDefinition = "TEXT")
    private String answers;

    @Column(name = "top_match_party")
    private String topMatchParty;

    @Column(name = "top_match_percentage")
    private Double topMatchPercentage;

    @Column(name = "second_match_party")
    private String secondMatchParty;

    @Column(name = "second_match_percentage")
    private Double secondMatchPercentage;

    @Column(name = "third_match_party")
    private String thirdMatchParty;

    @Column(name = "third_match_percentage")
    private Double thirdMatchPercentage;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public SurveyResult() {
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
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

    public String getSecondMatchParty() {
        return secondMatchParty;
    }

    public void setSecondMatchParty(String secondMatchParty) {
        this.secondMatchParty = secondMatchParty;
    }

    public Double getSecondMatchPercentage() {
        return secondMatchPercentage;
    }

    public void setSecondMatchPercentage(Double secondMatchPercentage) {
        this.secondMatchPercentage = secondMatchPercentage;
    }

    public String getThirdMatchParty() {
        return thirdMatchParty;
    }

    public void setThirdMatchParty(String thirdMatchParty) {
        this.thirdMatchParty = thirdMatchParty;
    }

    public Double getThirdMatchPercentage() {
        return thirdMatchPercentage;
    }

    public void setThirdMatchPercentage(Double thirdMatchPercentage) {
        this.thirdMatchPercentage = thirdMatchPercentage;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setId(long l) {
    }
}