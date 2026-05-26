package com.sop.backend.Account;

import com.nimbusds.jose.JOSEException;
import com.sop.backend.models.Chat;
import com.sop.backend.models.Discussion;
import com.sop.backend.models.User;
import com.sop.backend.repositories.ChatRepository;
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
class GetChatsOfUserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private String validToken;
    private User testUser;
    private User otherUser;
    private Discussion testDiscussion;

    @BeforeEach
    void setup() throws JOSEException {
        chatRepository.deleteAll();
        discussionRepository.deleteAll();
        surveyResultRepository.deleteAll();
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

        testDiscussion = new Discussion();
        testDiscussion.setTitle("Test Discussion");
        testDiscussion.setContent("Test content");
        testDiscussion = discussionRepository.save(testDiscussion);
    }

    @Test
    void testGetChatsOfUserEndpoint_UserHasChats_ReturnsAllChats() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Chat chat = new Chat();
            chat.setContent("Chat message " + i);
            chat.setUser(testUser);
            chat.setDiscussion(testDiscussion);
            chat.setUpvotes(i);
            chat.setDownvotes(0);
            chatRepository.save(chat);
        }

        mockMvc.perform(get("/api/chats/user/{userId}", testUser.getId())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[*].content", everyItem(containsString("Chat message"))))
                .andExpect(jsonPath("$.content[0].discussion_title", equalTo("Test Discussion")))
                .andExpect(jsonPath("$.content[0].upvotes", notNullValue()))
                .andExpect(jsonPath("$.totalElements", equalTo(3)))
                .andExpect(jsonPath("$.totalPages", equalTo(1)));
    }

    @Test
    void testGetChatsOfUserEndpoint_UserHasNoChats_ReturnsEmptyPage() throws Exception {
        mockMvc.perform(get("/api/chats/user/{userId}", testUser.getId())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", equalTo(0)))
                .andExpect(jsonPath("$.empty", equalTo(true)));
    }

    @Test
    void testGetChatsOfUserEndpoint_MultipleDiscussions_ReturnsCorrectDiscussionTitles() throws Exception {
        Discussion discussion2 = new Discussion();
        discussion2.setTitle("Another Discussion");
        discussion2.setContent("Another content");
        discussion2 = discussionRepository.save(discussion2);

        Chat chat1 = new Chat();
        chat1.setContent("Chat 1");
        chat1.setUser(testUser);
        chat1.setDiscussion(testDiscussion);
        chatRepository.save(chat1);

        Chat chat2 = new Chat();
        chat2.setContent("Chat 2");
        chat2.setUser(testUser);
        chat2.setDiscussion(discussion2);
        chatRepository.save(chat2);

        mockMvc.perform(get("/api/chats/user/{userId}", testUser.getId())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].discussion_title",
                        hasItems("Test Discussion", "Another Discussion")))
                .andExpect(jsonPath("$.content[*].discussion_id", everyItem(notNullValue())));
    }

    @Test
    void testGetChatsOfUserEndpoint_OtherUsersChatsNotReturned() throws Exception {
        Chat chat1 = new Chat();
        chat1.setContent("My chat");
        chat1.setUser(testUser);
        chat1.setDiscussion(testDiscussion);
        chatRepository.save(chat1);

        Chat chat2 = new Chat();
        chat2.setContent("Other user chat");
        chat2.setUser(otherUser);
        chat2.setDiscussion(testDiscussion);
        chatRepository.save(chat2);

        mockMvc.perform(get("/api/chats/user/{userId}", testUser.getId())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].content", equalTo("My chat")))
                .andExpect(jsonPath("$.totalElements", equalTo(1)));
    }

    @Test
    void testGetChatsOfUserEndpoint_PaginationFirstPage() throws Exception {
        for (int i = 1; i <= 25; i++) {
            Chat chat = new Chat();
            chat.setContent("Chat " + i);
            chat.setUser(testUser);
            chat.setDiscussion(testDiscussion);
            chat.setUpvotes(0);
            chat.setDownvotes(0);
            chatRepository.save(chat);
        }

        mockMvc.perform(get("/api/chats/user/{userId}", testUser.getId())
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
    void testGetChatsOfUserEndpoint_PaginationSecondPage() throws Exception {
        for (int i = 1; i <= 25; i++) {
            Chat chat = new Chat();
            chat.setContent("Chat " + i);
            chat.setUser(testUser);
            chat.setDiscussion(testDiscussion);
            chat.setUpvotes(0);
            chat.setDownvotes(0);
            chatRepository.save(chat);
        }

        mockMvc.perform(get("/api/chats/user/{userId}", testUser.getId())
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
    void testGetChatsOfUserEndpoint_NonExistentUserId_ReturnsEmptyPage() throws Exception {
        Long nonExistentUserId = 9999999990000L;

        mockMvc.perform(get("/api/chats/user/{userId}", nonExistentUserId)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", equalTo(0)));
    }
}
