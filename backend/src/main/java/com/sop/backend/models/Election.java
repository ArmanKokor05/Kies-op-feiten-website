package com.sop.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "elections")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String electionId;
    private String electionName;
    private String electionCategory;
    private LocalDate electionDate;


    private Integer electionYear;

    private String region;
    private String regionId;
    private String municipality;
    private String municipalityId;
    private Integer totalCounted;

    @Column(name = "`cast`")
    private Integer cast;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("election")
    private List<PartyResult> partyResults = new ArrayList<>();

    public Election() {}



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getElectionId() {
        return electionId;
    }

    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }

    public String getElectionName() {
        return electionName;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    public String getElectionCategory() {
        return electionCategory;
    }

    public void setElectionCategory(String electionCategory) {
        this.electionCategory = electionCategory;
    }

    public LocalDate getElectionDate() {
        return electionDate;
    }


    public void setElectionDate(LocalDate electionDate) {
        this.electionDate = electionDate;
        if (electionDate != null) {
            this.electionYear = electionDate.getYear();
        }
    }

    public Integer getElectionYear() {
        return electionYear;
    }

    public void setElectionYear(Integer electionYear) {
        this.electionYear = electionYear;
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

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(String municipalityId) {
        this.municipalityId = municipalityId;
    }

    public Integer getTotalCounted() {
        return totalCounted;
    }

    public void setTotalCounted(Integer totalCounted) {
        this.totalCounted = totalCounted;
    }

    public Integer getCast() {
        return cast;
    }

    public void setCast(Integer cast) {
        this.cast = cast;
    }

    public List<PartyResult> getPartyResults() {
        return partyResults;
    }

    public void setPartyResults(List<PartyResult> partyResults) {
        this.partyResults = partyResults;
    }

    public void addPartyResult(PartyResult result) {
        partyResults.add(result);
        result.setElection(this);
    }
}