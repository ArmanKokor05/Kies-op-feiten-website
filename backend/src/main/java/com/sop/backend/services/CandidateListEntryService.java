package com.sop.backend.services;

import com.sop.backend.models.Candidate;
import com.sop.backend.models.CandidateListEntry;
import com.sop.backend.models.Election2;
import com.sop.backend.models.Party2;
import com.sop.backend.repositories.CandidateListEntryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CandidateListEntryService {
    private final CandidateListEntryRepository candidateListEntryRepository;

    public CandidateListEntryService(CandidateListEntryRepository candidateListEntryRepository) {
        this.candidateListEntryRepository = candidateListEntryRepository;
    }

    public CandidateListEntry createCandidateListEntry(Candidate candidate, Election2 election, Party2 party) {
        CandidateListEntry candidateListEntry = new CandidateListEntry();

        candidateListEntry.setCandidate(candidate);
        candidateListEntry.setElection(election);
        candidateListEntry.setParty(party);
        candidateListEntry.setCandidateIdentifier(candidate.getCandidateId());

        return candidateListEntry;
    }

    public CandidateListEntry save(CandidateListEntry candidateListEntry) {
        return candidateListEntryRepository.findByCandidateAndElectionAndParty(candidateListEntry.getCandidate(), candidateListEntry.getElection(), candidateListEntry.getParty())
                .orElseGet(() -> candidateListEntryRepository.save(candidateListEntry));
    }

    public Page<CandidateListEntry> findByElectionAndParty(long electionId, long partyId, Pageable pageable) {
        return  candidateListEntryRepository
                .findByElectionIdAndPartyId(electionId, partyId, pageable);
    }
}
