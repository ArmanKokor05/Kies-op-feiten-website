package com.sop.backend.dto;

public class MatchResultDTO {
    private String partyName;
    private String partyColor;
    private double matchPercentage;

    public MatchResultDTO() {
    }

    public MatchResultDTO(String partyName, String partyColor, double matchPercentage) {
        this.partyName = partyName;
        this.partyColor = partyColor;
        this.matchPercentage = matchPercentage;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getPartyColor() {
        return partyColor;
    }

    public void setPartyColor(String partyColor) {
        this.partyColor = partyColor;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }
}