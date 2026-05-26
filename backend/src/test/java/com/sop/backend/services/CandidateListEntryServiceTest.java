package com.sop.backend.services;

import com.sop.backend.models.Candidate;
import com.sop.backend.models.CandidateListEntry;
import com.sop.backend.models.Election2;
import com.sop.backend.models.Party2;
import com.sop.backend.repositories.CandidateListEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateListEntryServiceTest {

    @Mock
    CandidateListEntryRepository candidateListEntryRepository;

    @InjectMocks
    CandidateListEntryService candidateListEntryService;

    @Test
    void createCandidateListEntry_setsAllFieldsCorrectly() {
        Candidate candidate = new Candidate();
        candidate.setCandidateId(123);
        Election2 election = new Election2();
        Party2 party = new Party2();

        CandidateListEntry entry =
                candidateListEntryService.createCandidateListEntry(candidate, election, party);

        assertNotNull(entry);
        assertEquals(candidate, entry.getCandidate());
        assertEquals(election, entry.getElection());
        assertEquals(party, entry.getParty());
        assertEquals(candidate.getCandidateId(), entry.getCandidateIdentifier());
    }

    @Test
    void save_returnsExisting_ifPresent() {
        Candidate candidate = new Candidate();
        Election2 election = new Election2();
        Party2 party = new Party2();

        CandidateListEntry existingEntry = new CandidateListEntry();
        existingEntry.setCandidate(candidate);
        existingEntry.setElection(election);
        existingEntry.setParty(party);

        when(candidateListEntryRepository
                .findByCandidateAndElectionAndParty(candidate, election, party))
                .thenReturn(Optional.of(existingEntry));

        CandidateListEntry result =
                candidateListEntryService.save(existingEntry);

        assertSame(existingEntry, result);
        verify(candidateListEntryRepository, never()).save(any());
    }

    @Test
    void save_savesIfNotPresent() {
        Candidate candidate = new Candidate();
        Election2 election = new Election2();
        Party2 party = new Party2();

        CandidateListEntry entry = new CandidateListEntry();
        entry.setCandidate(candidate);
        entry.setElection(election);
        entry.setParty(party);

        when(candidateListEntryRepository
                .findByCandidateAndElectionAndParty(candidate, election, party))
                .thenReturn(Optional.empty());

        when(candidateListEntryRepository.save(entry)).thenReturn(entry);

        CandidateListEntry result =
                candidateListEntryService.save(entry);

        assertSame(entry, result);
        verify(candidateListEntryRepository, times(1)).save(entry);
    }

    @Test
    void findByElectionAndParty_returnsPagedResults() {
        CandidateListEntry entry1 = new CandidateListEntry();
        CandidateListEntry entry2 = new CandidateListEntry();
        List<CandidateListEntry> entries = List.of(entry1, entry2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CandidateListEntry> page = new PageImpl<>(entries);

        when(candidateListEntryRepository
                .findByElectionIdAndPartyId(1L, 2L, pageable))
                .thenReturn(page);

        Page<CandidateListEntry> result =
                candidateListEntryService.findByElectionAndParty(1L, 2L, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(entries, result.getContent());
    }
}

