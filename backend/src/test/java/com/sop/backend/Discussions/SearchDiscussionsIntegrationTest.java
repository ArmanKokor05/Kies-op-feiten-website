package com.sop.backend.Discussions;

import com.nimbusds.jose.JOSEException;
import com.sop.backend.models.Discussion;
import com.sop.backend.models.User;
import com.sop.backend.repositories.DiscussionRepository;
import com.sop.backend.repositories.SurveyResultRepository;
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
class SearchDiscussionsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private String validToken;
    private User testUser;

    @BeforeEach
    void setup() throws JOSEException {
        discussionRepository.deleteAll();
        surveyResultRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser = userRepository.save(testUser);

        validToken = jwtUtil.generateToken(testUser);
    }

    @Test
    void testSearchDiscussions_WithMatchingTitle_ReturnsMatchingDiscussions() throws Exception {
        Discussion discussion1 = new Discussion();
        discussion1.setTitle("Spring Boot Tutorial");
        discussion1.setContent("Learn Spring Boot");
        discussion1.setUser(testUser);
        discussionRepository.save(discussion1);

        Discussion discussion2 = new Discussion();
        discussion2.setTitle("Spring Security Guide");
        discussion2.setContent("Secure your app");
        discussion2.setUser(testUser);
        discussionRepository.save(discussion2);

        Discussion discussion3 = new Discussion();
        discussion3.setTitle("Java Basics");
        discussion3.setContent("Introduction to Java");
        discussion3.setUser(testUser);
        discussionRepository.save(discussion3);

        mockMvc.perform(get("/api/discussions/search")
                        .header("Authorization", "Bearer " + validToken)
                        .param("title", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].title", everyItem(containsStringIgnoringCase("Spring"))))
                .andExpect(jsonPath("$.totalElements", equalTo(2)));
    }

    @Test
    void testSearchDiscussions_CaseInsensitive_ReturnsAllMatches() throws Exception {
        Discussion discussion1 = new Discussion();
        discussion1.setTitle("Java Basics");
        discussion1.setContent("Content 1");
        discussion1.setUser(testUser);
        discussionRepository.save(discussion1);

        Discussion discussion2 = new Discussion();
        discussion2.setTitle("JAVA Advanced");
        discussion2.setContent("Content 2");
        discussion2.setUser(testUser);
        discussionRepository.save(discussion2);

        Discussion discussion3 = new Discussion();
        discussion3.setTitle("Introduction to java");
        discussion3.setContent("Content 3");
        discussion3.setUser(testUser);
        discussionRepository.save(discussion3);

        mockMvc.perform(get("/api/discussions/search")
                        .header("Authorization", "Bearer " + validToken)
                        .param("title", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements", equalTo(3)));
    }

    @Test
    void testSearchDiscussions_NoMatches_ReturnsEmptyPage() throws Exception {
        Discussion discussion = new Discussion();
        discussion.setTitle("Spring Boot");
        discussion.setContent("Content");
        discussion.setUser(testUser);
        discussionRepository.save(discussion);

        mockMvc.perform(get("/api/discussions/search")
                        .header("Authorization", "Bearer " + validToken)
                        .param("title", "NonExistentTopic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", equalTo(0)))
                .andExpect(jsonPath("$.empty", equalTo(true)));
    }

    @Test
    void testSearchDiscussions_NoTitleParam_ReturnsAllDiscussions() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Discussion discussion = new Discussion();
            discussion.setTitle("Discussion " + i);
            discussion.setContent("Content " + i);
            discussion.setUser(testUser);
            discussionRepository.save(discussion);
        }

        mockMvc.perform(get("/api/discussions/search")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements", equalTo(3)));
    }

    @Test
    void testSearchDiscussions_PaginationFirstPage() throws Exception {
        for (int i = 1; i <= 25; i++) {
            Discussion discussion = new Discussion();
            discussion.setTitle("Discussion " + i);
            discussion.setContent("Content " + i);
            discussion.setUser(testUser);
            discussionRepository.save(discussion);
        }

        mockMvc.perform(get("/api/discussions/search")
                        .header("Authorization", "Bearer " + validToken)
                        .param("title", "Discussion")
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
    void testSearchDiscussions_PaginationSecondPage() throws Exception {
        for (int i = 1; i <= 25; i++) {
            Discussion discussion = new Discussion();
            discussion.setTitle("Discussion " + i);
            discussion.setContent("Content " + i);
            discussion.setUser(testUser);
            discussionRepository.save(discussion);
        }

        mockMvc.perform(get("/api/discussions/search")
                        .header("Authorization", "Bearer " + validToken)
                        .param("title", "Discussion")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements", equalTo(25)))
                .andExpect(jsonPath("$.totalPages", equalTo(2)))
                .andExpect(jsonPath("$.number", equalTo(1)))
                .andExpect(jsonPath("$.first", equalTo(false)))
                .andExpect(jsonPath("$.last", equalTo(true)));
    }

    @Test
    void testSearchDiscussions_ReturnsUserName() throws Exception {
        Discussion discussion = new Discussion();
        discussion.setTitle("Test Discussion");
        discussion.setContent("Test Content");
        discussion.setUser(testUser);
        discussionRepository.save(discussion);

        mockMvc.perform(get("/api/discussions/search")
                        .header("Authorization", "Bearer " + validToken)
                        .param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].userName", equalTo("John Doe")));
    }

    @Test
    void testSearchDiscussions_ReturnsUpvotesAndDownvotes() throws Exception {
        Discussion discussion = new Discussion();
        discussion.setTitle("Vote Discussion");
        discussion.setContent("Content");
        discussion.setUser(testUser);
        discussion.setUpvotes(10);
        discussion.setDownvotes(2);
        discussionRepository.save(discussion);

        mockMvc.perform(get("/api/discussions/search")
                        .header("Authorization", "Bearer " + validToken)
                        .param("title", "Vote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].upvotes", equalTo(10)))
                .andExpect(jsonPath("$.content[0].downvotes", equalTo(2)));
    }
}