package com.sop.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidate_list_entries")
public class CandidateListEntry extends BaseEntity {
    private int candidateIdentifier;
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election2 election;
    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party2 party;

    public Party2 getParty() {
        return party;
    }

    public void setParty(Party2 party) {
        this.party = party;
    }

    public Election2 getElection() { return election; }

    public void setElection(Election2 election) {
        this.election = election;
    }

    public Candidate  getCandidate() { return candidate; }

    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public int getCandidateIdentifier() { return candidateIdentifier; }

    public void setCandidateIdentifier(int candidateIdentifier) { this.candidateIdentifier = candidateIdentifier; }
}
