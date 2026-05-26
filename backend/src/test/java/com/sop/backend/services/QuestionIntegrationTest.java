package com.sop.backend.services;

import com.sop.backend.BackendApplication;
import com.sop.backend.models.Question;
import com.sop.backend.repositories.PartyStandpointRepository;
import com.sop.backend.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = BackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=test"
)
@AutoConfigureMockMvc
class QuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PartyStandpointRepository partyStandpointRepository;

    @BeforeEach
    void setUp() {
        partyStandpointRepository.deleteAll();
        questionRepository.deleteAll();

        Question q1 = new Question();
        q1.setTitle("Title 1");
        q1.setQuestion("Question 1");

        Question q2 = new Question();
        q2.setTitle("Title 2");
        q2.setQuestion("Question 2");

        questionRepository.saveAll(List.of(q1, q2));
    }

    @Test
    void api_GetAllQuestions_RetourneertAlleQuestions() throws Exception {
        mockMvc.perform(get("/questions"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.title=='Title 1')].question", hasItem("Question 1")))
                .andExpect(jsonPath("$[?(@.title=='Title 2')].question", hasItem("Question 2")));
    }

    @Test
    void api_GetAllQuestions_LegeLijstIndienGeenQuestions() throws Exception {
        questionRepository.deleteAll();

        mockMvc.perform(get("/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
