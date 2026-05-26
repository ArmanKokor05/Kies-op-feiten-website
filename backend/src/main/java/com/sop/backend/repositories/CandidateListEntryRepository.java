package com.sop.backend.repositories;

import com.sop.backend.models.Candidate;
import com.sop.backend.models.CandidateListEntry;
import com.sop.backend.models.Election2;
import com.sop.backend.models.Party2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateListEntryRepository extends JpaRepository<CandidateListEntry, Long> {
    Page<CandidateListEntry> findByElectionIdAndPartyId(long partyId, long electionId, Pageable pageable);

    Optional<CandidateListEntry> findByCandidateAndElectionAndParty(Candidate candidate, Election2 election, Party2 party);
}
