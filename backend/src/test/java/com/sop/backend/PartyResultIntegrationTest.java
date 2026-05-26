package com.sop.backend;

import com.sop.backend.models.Election;
import com.sop.backend.models.PartyResult;
import com.sop.backend.repositories.ElectionRepository;
import com.sop.backend.repositories.PartyResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PartyResultIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PartyResultRepository partyResultRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @BeforeEach
    void setup() {
        partyResultRepository.deleteAll();
        electionRepository.deleteAll();

        Election election2023 = new Election();
        election2023.setRegionId("alle");
        election2023.setElectionDate(LocalDate.of(2023, 3, 15));
        electionRepository.save(election2023);

        PartyResult p1 = new PartyResult();
        p1.setPartyName("PVV");
        p1.setPartyId("pvv");
        p1.setValidVotes(2345473);
        p1.setElection(election2023);

        PartyResult p2 = new PartyResult();
        p2.setPartyName("GL-PvdA");
        p2.setPartyId("gl-pvda");
        p2.setValidVotes(2093977);
        p2.setElection(election2023);

        PartyResult p3 = new PartyResult();
        p3.setPartyName("VVD");
        p3.setPartyId("vvd");
        p3.setValidVotes(1530707);
        p3.setElection(election2023);

        partyResultRepository.saveAll(Arrays.asList(p1, p2, p3));

        Election election2021 = new Election();
        election2021.setRegionId("alle");
        election2021.setElectionDate(LocalDate.of(2021, 3, 17));
        electionRepository.save(election2021);

        PartyResult p2021 = new PartyResult();
        p2021.setPartyName("VVD");
        p2021.setValidVotes(2000000);
        p2021.setElection(election2021);
        partyResultRepository.save(p2021);
    }

    @Test
    void api_GetSeats2023_RetourneertJSONMetBerekendezZetels() throws Exception {
        mockMvc.perform(get("/api/party-results/seats/2023"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].partyName", is("PVV")))
                .andExpect(jsonPath("$[0].validVotes", is(2345473)))
                .andExpect(jsonPath("$[0].seats", notNullValue()))
                .andExpect(jsonPath("$[1].partyName", is("GL-PvdA")))
                .andExpect(jsonPath("$[2].partyName", is("VVD")));
    }

    @Test
    void api_GetSeats2023_BerekentCorrectAantal() throws Exception {
        mockMvc.perform(get("/api/party-results/seats/2023"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seats", greaterThan(0)))
                .andExpect(jsonPath("$[1].seats", greaterThan(0)))
                .andExpect(jsonPath("$[2].seats", greaterThan(0)));

    }

    @Test
    void api_GetSeats1999_GeeftLegeLijst() throws Exception {
        mockMvc.perform(get("/api/party-results/seats/1999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void api_GetYears_RetourneertBeschikbareJaren() throws Exception {
        mockMvc.perform(get("/api/party-results/years"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$", hasItem(2023)))
                .andExpect(jsonPath("$", hasItem(2021)));
    }

    @Test
    void api_GetSeats_FiltertReginonaleResultaten() throws Exception {
        Election regionaal = new Election();
        regionaal.setRegionId("amsterdam");
        regionaal.setElectionDate(LocalDate.of(2023, 3, 15));
        electionRepository.save(regionaal);

        PartyResult regionaalResult = new PartyResult();
        regionaalResult.setPartyName("Regionale Partij");
        regionaalResult.setValidVotes(100000);
        regionaalResult.setElection(regionaal);
        partyResultRepository.save(regionaalResult);

        mockMvc.perform(get("/api/party-results/seats/2023"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].partyName", not(hasItem("Regionale Partij"))));
    }

    @Test
    void api_GeenAuthenticatieNodig() throws Exception {
        mockMvc.perform(get("/api/party-results/seats/2023"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/party-results/years"))
                .andExpect(status().isOk());
    }
}