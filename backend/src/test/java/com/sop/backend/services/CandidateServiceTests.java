package com.sop.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.models.Candidate;
import com.sop.backend.models.CandidateListEntry;
import com.sop.backend.models.Election2;
import com.sop.backend.models.Party2;
import com.sop.backend.repositories.CandidateRepository;
import com.sop.backend.xml.XmlToJsonParser;
import com.sop.backend.xml.mappers.CandidateMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTests {

    @Mock
    CandidateRepository candidateRepository;

    @Mock
    XmlToJsonParser xmlToJsonParser;

    @Mock
    CandidateMapper candidateMapper;

    @Mock
    ElectionService electionService;

    @Mock
    PartyService partyService;

    @Mock
    CandidateListEntryService candidateListEntryService;

    @InjectMocks
    CandidateService candidateService;

    @Test
    void createCandidate_parsesXml_savesCandidatesAndReturnsAmount() throws IOException {
        // prepare
        MultipartFile[] files = new MultipartFile[0];
        JsonNode fileNode = mock(JsonNode.class);
        when(xmlToJsonParser.parseMultipleFilesToJson(files)).thenReturn(List.of(fileNode));

        Election2 election = mock(Election2.class);
        when(electionService.parseFromJson(fileNode)).thenReturn(election);
        when(electionService.save(election)).thenReturn(election);

        JsonNode affiliationNode = mock(JsonNode.class);
        when(partyService.getAffiliationNodes(fileNode)).thenReturn(List.of(affiliationNode));

        // create a candidate mock and its getters
        Candidate candidate = mock(Candidate.class);
        when(candidate.getFirstName()).thenReturn("John");
        when(candidate.getLastName()).thenReturn("Doe");
        when(candidate.getGender()).thenReturn("M");
        when(candidate.getInitials()).thenReturn("J.D.");
        when(candidate.getQualifyingAddress()).thenReturn("Some Address");

        when(candidateMapper.assembleModel(affiliationNode)).thenReturn(List.of(candidate));

        // repository returns empty -> save will be called
        when(candidateRepository.findByFirstNameAndLastNameAndGenderAndInitialsAndQualifyingAddress(
                anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(Optional.empty());
        when(candidateRepository.save(candidate)).thenReturn(candidate);

        Party2 party = mock(Party2.class);
        when(partyService.getCandidateParty(affiliationNode)).thenReturn(party);
        when(partyService.save(party)).thenReturn(party);

        CandidateListEntry listEntry = mock(CandidateListEntry.class);
        when(candidateListEntryService.createCandidateListEntry(candidate, election, party)).thenReturn(listEntry);

        // act
        var dto = candidateService.createCandidate(files);

        // assert
        assertNotNull(dto);
        assertEquals(1, dto.getAmount());

        // verify saves were called
        verify(candidateRepository, times(1)).save(candidate);
        verify(candidateListEntryService, times(1)).save(listEntry);
    }

    @Test
    void save_returnsExistingCandidate_ifFound() {
        Candidate candidate = mock(Candidate.class);
        when(candidate.getFirstName()).thenReturn("Jane");
        when(candidate.getLastName()).thenReturn("Roe");
        when(candidate.getGender()).thenReturn("F");
        when(candidate.getInitials()).thenReturn("J.R.");
        when(candidate.getQualifyingAddress()).thenReturn("Addr");

        when(candidateRepository.findByFirstNameAndLastNameAndGenderAndInitialsAndQualifyingAddress(
                anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(Optional.of(candidate));

        Candidate result = candidateService.save(candidate);

        assertSame(candidate, result);
        verify(candidateRepository, never()).save(any());
    }

    @Test
    void saveAll_savesEachCandidate_andCreatesRelationalModels() {
        Candidate candidate = mock(Candidate.class);
        when(candidate.getFirstName()).thenReturn("A");
        when(candidate.getLastName()).thenReturn("B");
        when(candidate.getGender()).thenReturn("X");
        when(candidate.getInitials()).thenReturn("AB");
        when(candidate.getQualifyingAddress()).thenReturn("Addr");

        when(candidateRepository.findByFirstNameAndLastNameAndGenderAndInitialsAndQualifyingAddress(
                anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(Optional.empty());

        when(candidateRepository.save(candidate)).thenReturn(candidate);

        Election2 election = mock(Election2.class);
        Party2 party = mock(Party2.class);

        CandidateListEntry listEntry = mock(CandidateListEntry.class);
        when(candidateListEntryService.createCandidateListEntry(candidate, election, party)).thenReturn(listEntry);

        candidateService.saveAll(List.of(candidate), election, party);

        verify(candidateRepository, times(1)).save(candidate);
        verify(candidateListEntryService, times(1)).createCandidateListEntry(candidate, election, party);
        verify(candidateListEntryService, times(1)).save(listEntry);
    }
}
