package com.sop.backend.Account;

import com.nimbusds.jose.JOSEException;
import com.sop.backend.models.User;
import com.sop.backend.repositories.DiscussionRepository;
import com.sop.backend.repositories.UserRepository;
import com.sop.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeleteAccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private String validToken;

    @BeforeEach
    void setup() throws JOSEException {
        discussionRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        validToken = jwtUtil.generateToken(user);
    }

    @Test
    void testDeleteAccountEndpoint_UserExists_AccountDeleted() throws Exception {
        User user = userRepository.findByEmail("john@example.com").orElseThrow();
        Long userId = user.getId();

        mockMvc.perform(delete("/api/users/{userId}", userId)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deleted successfully"));

        org.junit.jupiter.api.Assertions.assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    void testDeleteAccountEndpoint_UserNotFound_ReturnsError() throws Exception {
        Long nonExistentUserId = 9999L;

        mockMvc.perform(delete("/api/users/{userId}", nonExistentUserId)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User not found")));
    }
}