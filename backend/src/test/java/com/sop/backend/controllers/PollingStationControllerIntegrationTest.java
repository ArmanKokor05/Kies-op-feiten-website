package com.sop.backend.controllers;

import com.sop.backend.models.*;
import com.sop.backend.repositories.*;
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
class PollingStationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MunicipalityRepository municipalityRepository;

    @Autowired
    private PollingStationRepository pollingStationRepository;

    @Autowired
    private Election2Repository electionRepository;

    @Autowired
    private PollingStationElectionRepository pollingStationElectionRepository;

    private Municipality municipality;
    private Election2 election;

    @BeforeEach
    void setup() {
        // Create municipality
        municipality = new Municipality();
        municipality.setName("Test Municipality");
        municipalityRepository.save(municipality);

        // Create election
        election = new Election2();
        election.setName("Test Election");
        electionRepository.save(election);

        // Create polling station
        PollingStation pollingStation = new PollingStation();
        pollingStation.setName("Test Station");
        pollingStation.setZipcode("12345");
        pollingStation.setMunicipality(municipality);
        pollingStationRepository.save(pollingStation);

        // Link polling station to election
        PollingStationElection pse = new PollingStationElection();
        pse.setPollingStation(pollingStation);
        pse.setElection(election);
        pollingStationElectionRepository.save(pse);
    }

    @Test
    void getPollingStationsByElection_returnsPagedResults() throws Exception {
        mockMvc.perform(get("/api/pollingstation/election")
                        .param("electionId", election.getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Polling stations fetched successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].name").value("Test Station"))
                .andExpect(jsonPath("$.data.content[0].zipcode").value("12345"))
                .andExpect(jsonPath("$.data.content[0].municipality").value("Test Municipality"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void getPollingStationsByMunicipality_returnsPagedResults() throws Exception {
        mockMvc.perform(get("/api/pollingstation/municipality")
                        .param("electionId", election.getId().toString())
                        .param("municipalityId", municipality.getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Polling stations fetched successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].name").value("Test Station"))
                .andExpect(jsonPath("$.data.content[0].zipcode").value("12345"))
                .andExpect(jsonPath("$.data.content[0].municipality").value("Test Municipality"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
}
