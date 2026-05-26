package com.sop.backend.Account;

import com.nimbusds.jose.JOSEException;
import com.sop.backend.models.Discussion;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GetDiscussionsOfUserIntegrationTest {

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
    private User testUser;
    private User otherUser;

    @BeforeEach
    void setup() throws JOSEException {
        discussionRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser = userRepository.save(testUser);

        validToken = jwtUtil.generateToken(testUser);

        otherUser = new User();
        otherUser.setName("Jane Doe");
        otherUser.setEmail("jane@example.com");
        otherUser.setPassword(passwordEncoder.encode("password123"));
        otherUser = userRepository.save(otherUser);
    }

    @Test
    void testGetDiscussionsOfUserEndpoint_UserHasDiscussions_ReturnsAllDiscussions() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Discussion discussion = new Discussion();
            discussion.setTitle("Discussion " + i);
            discussion.setContent("Content for discussion " + i);
            discussion.setUser(testUser);
            discussion.setUpvotes(i);
            discussion.setDownvotes(0);
            discussionRepository.save(discussion);
        }

        mockMvc.perform(get("/api/discussions/user/{userId}", testUser.getId())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[*].title", everyItem(containsString("Discussion"))))
                .andExpect(jsonPath("$.content[*].content", everyItem(containsString("Content for discussion"))))
                .andExpect(jsonPath("$.content[0].upvotes", notNullValue()))
                .andExpect(jsonPath("$.totalElements", equalTo(3)))
                .andExpect(jsonPath("$.totalPages", equalTo(1)));
    }

    @Test
    void testGetDiscussionsOfUserEndpoint_UserHasNoDiscussions_ReturnsEmptyPage() throws Exception {
        mockMvc.perform(get("/api/discussions/user/{userId}", testUser.getId())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", equalTo(0)))
                .andExpect(jsonPath("$.empty", equalTo(true)));
    }

    @Test
    void testGetDiscussionsOfUserEndpoint_OtherUsersDiscussionsNotReturned() throws Exception {
        Discussion discussion1 = new Discussion();
        discussion1.setTitle("My Discussion");
        discussion1.setContent("My content");
        discussion1.setUser(testUser);
        discussionRepository.save(discussion1);

        Discussion discussion2 = new Discussion();
        discussion2.setTitle("Other User Discussion");
        discussion2.setContent("Other user content");
        discussion2.setUser(otherUser);
        discussionRepository.save(discussion2);

        mockMvc.perform(get("/api/discussions/user/{userId}", testUser.getId())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", equalTo("My Discussion")))
                .andExpect(jsonPath("$.totalElements", equalTo(1)));
    }

    @Test
    void testGetDiscussionsOfUserEndpoint_PaginationFirstPage() throws Exception {
        for (int i = 1; i <= 25; i++) {
            Discussion discussion = new Discussion();
            discussion.setTitle("Discussion " + i);
            discussion.setContent("Content " + i);
            discussion.setUser(testUser);
            discussion.setUpvotes(0);
            discussion.setDownvotes(0);
            discussionRepository.save(discussion);
        }

        mockMvc.perform(get("/api/discussions/user/{userId}", testUser.getId())
                        .header("Authorization", "Bearer " + validToken)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(20)))
                .andExpect(jsonPath("$.totalElements", equalTo(25)))
                .andExpect(jsonPath("$.totalPages", equalTo(2)))
                .andExpect(jsonPath("$.number", equalTo(0)))
                .andExpect(jsonPath("$.first", equalTo(true)))
                .andExpect(jsonPath("$.last", equalTo(false)));
    }

    @Test
    void testGetDiscussionsOfUserEndpoint_PaginationSecondPage() throws Exception {
        for (int i = 1; i <= 25; i++) {
            Discussion discussion = new Discussion();
            discussion.setTitle("Discussion " + i);
            discussion.setContent("Content " + i);
            discussion.setUser(testUser);
            discussion.setUpvotes(0);
            discussion.setDownvotes(0);
            discussionRepository.save(discussion);
        }

        mockMvc.perform(get("/api/discussions/user/{userId}", testUser.getId())
                        .header("Authorization", "Bearer " + validToken)
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements", equalTo(25)))
                .andExpect(jsonPath("$.number", equalTo(1)))
                .andExpect(jsonPath("$.first", equalTo(false)))
                .andExpect(jsonPath("$.last", equalTo(true)));
    }

    @Test
    void testGetDiscussionsOfUserEndpoint_NonExistentUserId_ReturnsEmptyPage() throws Exception {
        Long nonExistentUserId = 9999999990000L;

        mockMvc.perform(get("/api/discussions/user/{userId}", nonExistentUserId)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", equalTo(0)));
    }
}