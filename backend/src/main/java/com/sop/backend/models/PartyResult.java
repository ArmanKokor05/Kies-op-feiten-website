package com.sop.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "party_results")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PartyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String partyId;
    private String partyName;
    private Integer validVotes;
    private Integer seats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    @JsonIgnoreProperties("partyResults")
    private Election election;

    @OneToMany(mappedBy = "partyResult", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("partyResult")
    private List<CandidateResult> candidateResults = new ArrayList<>();

    public PartyResult() {}

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

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public List<CandidateResult> getCandidateResults() {
        return candidateResults;
    }

    public void setCandidateResults(List<CandidateResult> candidateResults) {
        this.candidateResults = candidateResults;
    }

    public void addCandidateResult(CandidateResult result) {
        candidateResults.add(result);
        result.setPartyResult(this);
    }
}