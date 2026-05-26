package com.sop.backend.controllers;

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
class ElectionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllElections_returns200AndList() throws Exception {
        mockMvc.perform(get("/api/election"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("elections retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }
}
