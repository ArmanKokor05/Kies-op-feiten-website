package com.sop.backend.controllers;

import com.sop.backend.models.Candidate;
import com.sop.backend.models.CandidateListEntry;
import com.sop.backend.models.Election2;
import com.sop.backend.models.Party2;
import com.sop.backend.repositories.CandidateListEntryRepository;
import com.sop.backend.repositories.CandidateRepository;
import com.sop.backend.repositories.Election2Repository;
import com.sop.backend.repositories.Party2Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CandidateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private Election2Repository election2Repository;

    @Autowired
    private Party2Repository party2Repository;

    @Autowired
    private CandidateListEntryRepository candidateListEntryRepository;

    private Election2 election;
    private Party2 party;

    @BeforeEach
    void setup() {
        // Create election
        election = new Election2();
        election.setName("Test Election 2024");
        election2Repository.save(election);

        // Create party
        party = new Party2();
        party.setName("Partij voor de Vrijheid");
        party.setAcronym("PVV");
        party2Repository.save(party);

        // Create first candidate
        Candidate candidate1 = new Candidate();
        candidate1.setFirstName("Geert");
        candidate1.setLastName("Wilders");
        candidate1.setInitials("G.");
        candidate1.setGender("male");
        candidate1.setQualifyingAddress("'s-Gravenhage");
        candidate1.setCandidateId(1);
        candidateRepository.save(candidate1);

        // Create second candidate
        Candidate candidate2 = new Candidate();
        candidate2.setFirstName("Sebastiaan");
        candidate2.setLastName("Stöteler");
        candidate2.setInitials("T.S.M.");
        candidate2.setGender("male");
        candidate2.setQualifyingAddress("Almelo");
        candidate2.setCandidateId(2);
        candidateRepository.save(candidate2);

        // Link first candidate to election and party
        CandidateListEntry entry1 = new CandidateListEntry();
        entry1.setCandidate(candidate1);
        entry1.setElection(election);
        entry1.setParty(party);
        entry1.setCandidateIdentifier(1);
        candidateListEntryRepository.save(entry1);

        // Link second candidate to election and party
        CandidateListEntry entry2 = new CandidateListEntry();
        entry2.setCandidate(candidate2);
        entry2.setElection(election);
        entry2.setParty(party);
        entry2.setCandidateIdentifier(2);
        candidateListEntryRepository.save(entry2);
    }

    @Test
    void getCandidatesByElectionAndParty_returnsPagedResults() throws Exception {
        mockMvc.perform(get("/candidates")
                        .param("electionId", election.getId().toString())
                        .param("partyId", party.getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Candidates fetched successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].firstName").value("Geert"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Wilders"))
                .andExpect(jsonPath("$.data.content[0].initials").value("G."))
                .andExpect(jsonPath("$.data.content[0].gender").value("male"))
                .andExpect(jsonPath("$.data.content[0].qualifyingAdress").value("'s-Gravenhage"))
                .andExpect(jsonPath("$.data.content[1].firstName").value("Sebastiaan"))
                .andExpect(jsonPath("$.data.content[1].lastName").value("Stöteler"))
                .andExpect(jsonPath("$.data.content[1].initials").value("T.S.M."))
                .andExpect(jsonPath("$.data.content[1].gender").value("male"))
                .andExpect(jsonPath("$.data.content[1].qualifyingAdress").value("Almelo"))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void getCandidatesByElectionAndParty_returnsEmptyWhenNoMatches() throws Exception {
        // Create a different party
        Party2 differentParty = new Party2();
        differentParty.setName("Volkspartij voor Vrijheid en Democratie");
        differentParty.setAcronym("VVD");
        party2Repository.save(differentParty);

        mockMvc.perform(get("/candidates")
                        .param("electionId", election.getId().toString())
                        .param("partyId", differentParty.getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Candidates fetched successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void getCandidatesByElectionAndParty_respectsPagination() throws Exception {
        mockMvc.perform(get("/candidates")
                        .param("electionId", election.getId().toString())
                        .param("partyId", party.getId().toString())
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Candidates fetched successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(2));
    }
}