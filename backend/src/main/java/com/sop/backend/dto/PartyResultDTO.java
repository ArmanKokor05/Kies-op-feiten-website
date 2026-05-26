package com.sop.backend.dto;

public class PartyResultDTO {
    private Long id;
    private String partyId;
    private String partyName;
    private Integer validVotes;
    private Integer seats;

    private String region;
    private String regionId;
    private Integer electionYear;
    private String color;

    public PartyResultDTO() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public Integer getValidVotes() {
        return validVotes;
    }

    public void setValidVotes(Integer validVotes) {
        this.validVotes = validVotes;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public Integer getElectionYear() {
        return electionYear;
    }

    public void setElectionYear(Integer electionYear) {
        this.electionYear = electionYear;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}