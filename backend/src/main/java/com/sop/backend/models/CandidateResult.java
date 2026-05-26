package com.sop.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "candidate_results")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CandidateResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateId;
    private Integer validVotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_result_id")
    @JsonIgnoreProperties("candidateResults")
    private PartyResult partyResult;

    public CandidateResult() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public Integer getValidVotes() {
        return validVotes;
    }

    public void setValidVotes(Integer validVotes) {
        this.validVotes = validVotes;
    }

    public PartyResult getPartyResult() {
        return partyResult;
    }

    public void setPartyResult(PartyResult partyResult) {
        this.partyResult = partyResult;
    }
}