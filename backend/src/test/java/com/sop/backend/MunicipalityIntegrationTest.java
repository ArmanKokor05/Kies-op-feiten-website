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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MunicipalityIntegrationTest {

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

        Election gorinchem = new Election();
        gorinchem.setRegionId("14");
        gorinchem.setRegion("Dordrecht");
        gorinchem.setMunicipality("Gorinchem");
        gorinchem.setMunicipalityId("Gorinchem");
        gorinchem.setElectionDate(LocalDate.of(2023, 3, 15));
        electionRepository.save(gorinchem);

        PartyResult gorinchemResult = new PartyResult();
        gorinchemResult.setPartyId("4");
        gorinchemResult.setPartyName("PVV (Partij voor de Vrijheid)");
        gorinchemResult.setValidVotes(5155);
        gorinchemResult.setElection(gorinchem);
        partyResultRepository.save(gorinchemResult);

        Election lopik = new Election();
        lopik.setRegionId("16");
        lopik.setRegion("Lopik");
        lopik.setMunicipality("Lopik");
        lopik.setMunicipalityId("Lopik");
        lopik.setElectionDate(LocalDate.of(2023, 3, 15));
        electionRepository.save(lopik);

        PartyResult lopikResult = new PartyResult();
        lopikResult.setPartyId("6");
        lopikResult.setPartyName("VVD");
        lopikResult.setValidVotes(1446);
        lopikResult.setElection(lopik);
        partyResultRepository.save(lopikResult);
    }


    @Test
    void testGetAvailableMunicipalities_ReturnsAllMunicipalities() throws Exception {
        mockMvc.perform(get("/api/party-results/municipalities"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*]", hasItems("Gorinchem", "Lopik")));
    }

    @Test
    void testGetAvailableMunicipalities_ContainsGorinchem() throws Exception {
        mockMvc.perform(get("/api/party-results/municipalities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasItem("Gorinchem")));
    }

    @Test
    void testGetSeatsByYearAndMunicipality_FetchesGorinchemData() throws Exception {
        mockMvc.perform(get("/api/party-results/seats/2023/municipalities")
                        .param("municipality", "Gorinchem"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].partyName", is("PVV (Partij voor de Vrijheid)")))
                .andExpect(jsonPath("$[0].validVotes", is(5155)))
                .andExpect(jsonPath("$[0].region", is("Dordrecht")))
                .andExpect(jsonPath("$[0].regionId", is("14")));
    }

    @Test
    void testGetSeatsByYearAndMunicipality_FilteringWorksCorrectly() throws Exception {

        mockMvc.perform(get("/api/party-results/seats/2023/municipalities")
                        .param("municipality", "Gorinchem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].partyName", is("PVV (Partij voor de Vrijheid)")))
                .andExpect(jsonPath("$[0].partyName", not("VVD")));
    }

    @Test
    void testGetSeatsByYearAndMunicipality_NoDataForNonExistentMunicipality() throws Exception {
        mockMvc.perform(get("/api/party-results/seats/2023/municipalities")
                        .param("municipality", "Rotterdam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetSeatsByYearAndMunicipality_NoDataForDifferentYear() throws Exception {
        mockMvc.perform(get("/api/party-results/seats/2020/municipalities")
                        .param("municipality", "Gorinchem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetSeatsByYearAndMunicipality_DataContainsAllRequiredFields() throws Exception {
        mockMvc.perform(get("/api/party-results/seats/2023/municipalities")
                        .param("municipality", "Gorinchem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].partyId", notNullValue()))
                .andExpect(jsonPath("$[0].partyName", notNullValue()))
                .andExpect(jsonPath("$[0].validVotes", notNullValue()))
                .andExpect(jsonPath("$[0].seats", notNullValue()))
                .andExpect(jsonPath("$[0].region", notNullValue()))
                .andExpect(jsonPath("$[0].regionId", notNullValue()))
                .andExpect(jsonPath("$[0].electionYear", notNullValue()));
    }
}