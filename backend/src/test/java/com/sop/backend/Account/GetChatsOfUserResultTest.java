package com.sop.backend.Account;

import com.sop.backend.dto.ChatDTO;
import com.sop.backend.mapper.ChatMapper;
import com.sop.backend.models.Chat;
import com.sop.backend.models.Discussion;
import com.sop.backend.models.User;
import com.sop.backend.repositories.ChatRepository;
import com.sop.backend.repositories.DiscussionRepository;
import com.sop.backend.services.ChatService;
import com.sop.backend.services.NotificationService;
import com.sop.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetChatsOfUserResultTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserService userService;

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private NotificationService notificationService;

    private ChatService chatService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ChatMapper chatMapper = new ChatMapper();
        chatService = new ChatService(chatRepository, chatMapper, userService, discussionRepository, notificationService);
    }

    @Test
    void testGetChatsByUser_UserHasChats_ReturnsAllChats() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        List<Chat> chats = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            chats.add(createChat("Chat message " + i, i, 0, "Test Discussion"));
        }

        when(chatRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(chats));

        Page<ChatDTO> result = chatService.getChatsByUser(userId, pageable);

        assertEquals(3, result.getTotalElements());
        result.getContent().forEach(chatDTO ->
                assertTrue(chatDTO.getContent().contains("Chat message")));
        assertEquals("Test Discussion", result.getContent().get(0).getDiscussion_title());
    }

    @Test
    void testGetChatsByUser_UserHasNoChats_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 20);

        when(chatRepository.findByUserId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        Page<ChatDTO> result = chatService.getChatsByUser(1L, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetChatsByUser_MultipleDiscussions_ReturnsCorrectTitles() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        String[] titles = {"Discussion A", "Discussion B"};
        List<Chat> chats = new ArrayList<>();

        for (String title : titles) {
            chats.add(createChat("Chat for " + title, 0, 0, title));
        }

        when(chatRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(chats));

        Page<ChatDTO> result = chatService.getChatsByUser(userId, pageable);

        for (int i = 0; i < titles.length; i++) {
            assertEquals(titles[i], result.getContent().get(i).getDiscussion_title());
        }
    }

    @Test
    void testGetChatsByUser_OneChat_ReturnsOne() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        Chat chat = createChat("Single chat message", 5, 2, "Single Discussion");
        when(chatRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(List.of(chat)));

        Page<ChatDTO> result = chatService.getChatsByUser(userId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Single chat message", result.getContent().get(0).getContent());
        assertEquals("Single Discussion", result.getContent().get(0).getDiscussion_title());
    }

    @Test
    void testGetChatsByUser_ReturnsCorrectVotes() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        List<Chat> chats = List.of(
                createChat("Chat 1", 10, 2, "Discussion 1"),
                createChat("Chat 2", 5, 1, "Discussion 2")
        );

        when(chatRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(chats));

        Page<ChatDTO> result = chatService.getChatsByUser(userId, pageable);

        assertEquals(10, result.getContent().get(0).getUpvotes());
        assertEquals(2, result.getContent().get(0).getDownvotes());
        assertEquals(5, result.getContent().get(1).getUpvotes());
        assertEquals(1, result.getContent().get(1).getDownvotes());
    }

    @Test
    void testGetChatsByUser_NullUserId_ThrowsException() {
        Pageable pageable = PageRequest.of(0, 20);

        when(chatRepository.findByUserId(null, pageable))
                .thenThrow(new IllegalArgumentException("User ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                chatService.getChatsByUser(null, pageable)
        );
    }

    @Test
    void testGetChatsByUser_NegativeUserId_ThrowsException() {
        Long negativeUserId = -1L;
        Pageable pageable = PageRequest.of(0, 20);

        when(chatRepository.findByUserId(negativeUserId, pageable))
                .thenThrow(new IllegalArgumentException("User ID must be positive"));

        assertThrows(IllegalArgumentException.class, () ->
                chatService.getChatsByUser(negativeUserId, pageable)
        );
    }

    @Test
    void testGetChatsByUser_NullPageable_ThrowsException() {
        Long userId = 1L;

        when(chatRepository.findByUserId(userId, null))
                .thenThrow(new IllegalArgumentException("Pageable cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                chatService.getChatsByUser(userId, null)
        );
    }

    @Test
    void testGetChatsByUser_LargeDataSet_HandlesCorrectly() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 100);

        List<Chat> chats = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            chats.add(createChat("Chat " + i, 0, 0, "Discussion"));
        }

        when(chatRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(chats, pageable, 1000));

        Page<ChatDTO> result = chatService.getChatsByUser(userId, pageable);

        assertEquals(100, result.getNumberOfElements());
        assertEquals(1000, result.getTotalElements());
        assertNotNull(result.getContent());
    }

    @Test
    void testGetChatsByUser_SpecialCharacters_HandlesCorrectly() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        Chat chat = createChat(
                "Chat with <html> & special chars! émojis 🎉 @#$%",
                0, 0,
                "Discussion with <tags>"
        );

        when(chatRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(List.of(chat)));

        Page<ChatDTO> result = chatService.getChatsByUser(userId, pageable);

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getContent().contains("🎉"));
        assertTrue(result.getContent().get(0).getDiscussion_title().contains("<tags>"));
    }

    @Test
    void testGetChatsByUser_VeryLongContent_HandlesCorrectly() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        String longContent = "A".repeat(10000);
        Chat chat = createChat(longContent, 0, 0, "Discussion");

        when(chatRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(List.of(chat)));

        Page<ChatDTO> result = chatService.getChatsByUser(userId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(10000, result.getContent().get(0).getContent().length());
    }

    @Test
    void testGetChatsByUser_MultipleDifferentDiscussions_ReturnsAll() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        List<Chat> chats = List.of(
                createChat("Chat 1", 0, 0, "Java Discussion"),
                createChat("Chat 2", 0, 0, "Python Discussion"),
                createChat("Chat 3", 0, 0, "JavaScript Discussion")
        );

        when(chatRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(chats));

        Page<ChatDTO> result = chatService.getChatsByUser(userId, pageable);

        assertEquals(3, result.getTotalElements());
        assertEquals("Java Discussion", result.getContent().get(0).getDiscussion_title());
        assertEquals("Python Discussion", result.getContent().get(1).getDiscussion_title());
        assertEquals("JavaScript Discussion", result.getContent().get(2).getDiscussion_title());
    }


    private Chat createChat(String content, int upvotes, int downvotes, String discussionTitle) {
        Chat chat = new Chat();
        chat.setContent(content);
        chat.setUpvotes(upvotes);
        chat.setDownvotes(downvotes);

        Discussion discussion = new Discussion();
        discussion.setTitle(discussionTitle);

        User user = new User();
        user.setName("Test User");

        chat.setUser(user);
        chat.setDiscussion(discussion);

        return chat;
    }
}
