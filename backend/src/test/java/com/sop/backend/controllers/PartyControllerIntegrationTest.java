package com.sop.backend.controllers;

import com.sop.backend.models.Party2;
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
class PartyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Party2Repository party2Repository;

    @BeforeEach
    void setup() {
        // Create test parties
        Party2 party1 = new Party2();
        party1.setName("Partij voor de Vrijheid");
        party1.setAcronym("PVV");
        party2Repository.save(party1);

        Party2 party2 = new Party2();
        party2.setName("Volkspartij voor Vrijheid en Democratie");
        party2.setAcronym("VVD");
        party2Repository.save(party2);

        Party2 party3 = new Party2();
        party3.setName("Democraten 66");
        party3.setAcronym("D66");
        party2Repository.save(party3);
    }

    @Test
    void getParties_returnsAllParties() throws Exception {
        mockMvc.perform(get("/api/party"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Parties fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].name").value("Democraten 66"))
                .andExpect(jsonPath("$.data[0].acronym").value("D66"))
                .andExpect(jsonPath("$.data[1].name").value("Partij voor de Vrijheid"))
                .andExpect(jsonPath("$.data[1].acronym").value("PVV"))
                .andExpect(jsonPath("$.data[2].name").value("Volkspartij voor Vrijheid en Democratie"))
                .andExpect(jsonPath("$.data[2].acronym").value("VVD"));
    }

    @Test
    void getParties_returnsEmptyListWhenNoParties() throws Exception {
        party2Repository.deleteAll();

        mockMvc.perform(get("/api/party"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Parties fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}