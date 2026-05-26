package com.sop.backend.Account;

import com.nimbusds.jose.JOSEException;
import com.sop.backend.models.User;
import com.sop.backend.repositories.UserRepository;
import com.sop.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChangeUsernameIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private String validToken;

    @BeforeEach
    void setup() throws JOSEException {
        userRepository.deleteAll();

        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        validToken = jwtUtil.generateToken(user);
    }

    @Test
    void testChangeNameEndpoint_UserExists_NameChangedSuccessfully() throws Exception {
        User user = userRepository.findByEmail("john@example.com").orElseThrow();
        Long userId = user.getId();

        MvcResult result = mockMvc.perform(put("/api/users/{userId}/username", userId)
                        .contentType("application/json")
                        .content("{\"name\": \"Jane Doe\"}")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();
        org.junit.jupiter.api.Assertions.assertNotNull(token);
        org.junit.jupiter.api.Assertions.assertFalse(token.isEmpty());

        User updatedUser = userRepository.findByEmail("john@example.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("Jane Doe", updatedUser.getName());
    }

    @Test
    void testChangeNameEndpoint_UserNotFound_ReturnsError() throws Exception {
        Long nonExistentUserId = 9999L;

        mockMvc.perform(put("/api/users/{userId}/username", nonExistentUserId)
                        .contentType("application/json")
                        .content("{\"name\": \"Jane Doe\"}")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User not found")));
    }

    @Test
    void testChangeNameEndpoint_VerifyOnlyNameChanges() throws Exception {
        User user = userRepository.findByEmail("john@example.com").orElseThrow();
        Long userId = user.getId();
        String originalEmail = user.getEmail();

        mockMvc.perform(put("/api/users/{userId}/username", userId)
                        .contentType("application/json")
                        .content("{\"name\": \"Updated Name\"}")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(userId).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("Updated Name", updatedUser.getName());
        org.junit.jupiter.api.Assertions.assertEquals(originalEmail, updatedUser.getEmail());
    }

    @Test
    void testChangeNameEndpoint_ReturnsValidJWTToken() throws Exception {
        User user = userRepository.findByEmail("john@example.com").orElseThrow();
        Long userId = user.getId();

        MvcResult result = mockMvc.perform(put("/api/users/{userId}/username", userId)
                        .contentType("application/json")
                        .content("{\"name\": \"Jane Doe\"}")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        String token = com.jayway.jsonpath.JsonPath.read(json, "$.data");

        org.junit.jupiter.api.Assertions.assertNotNull(token);
        org.junit.jupiter.api.Assertions.assertFalse(token.isBlank());

        String[] parts = token.split("\\.");
        org.junit.jupiter.api.Assertions.assertEquals(3, parts.length, "JWT must contain header, payload and signature");

        String header = parts[0];
        String payload = parts[1];
        String signature = parts[2];

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> java.util.Base64.getUrlDecoder().decode(header));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> java.util.Base64.getUrlDecoder().decode(payload));

        String jsonPayload = new String(java.util.Base64.getUrlDecoder().decode(payload));
        org.junit.jupiter.api.Assertions.assertTrue(jsonPayload.trim().startsWith("{") && jsonPayload.trim().endsWith("}"),
                "JWT payload should be valid JSON");

        org.junit.jupiter.api.Assertions.assertFalse(signature.isBlank(), "JWT signature must not be empty");
    }
}