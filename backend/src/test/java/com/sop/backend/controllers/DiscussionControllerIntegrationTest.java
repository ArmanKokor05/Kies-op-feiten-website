package com.sop.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sop.backend.BackendApplication;
import com.sop.backend.dto.DiscussionDTO;
import com.sop.backend.models.User;
import com.sop.backend.repositories.UserRepository;
import com.sop.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = BackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=test"
)
@AutoConfigureMockMvc
public class DiscussionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String bearerToken;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() throws Exception {
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPassword("password");

        testUser = userRepository.save(testUser);

        bearerToken = jwtUtil.generateToken(testUser);
    }

    @Test
    void createDiscussion_returns200AndDiscussionDTO() throws Exception {
        DiscussionDTO discussionDTO = new DiscussionDTO();
        discussionDTO.setTitle("Test Discussion");
        discussionDTO.setContent("This is a test discussion content.");

        mockMvc.perform(post("/api/discussions/create")
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discussionDTO)))
                .andExpect(status().isOk());
    }
}
