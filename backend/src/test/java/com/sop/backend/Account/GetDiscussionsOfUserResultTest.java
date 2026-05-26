package com.sop.backend.Account;

import com.sop.backend.dto.DiscussionDTO;
import com.sop.backend.mapper.DiscussionMapper;
import com.sop.backend.models.Discussion;
import com.sop.backend.models.User;
import com.sop.backend.repositories.ChatRepository;
import com.sop.backend.repositories.DiscussionRepository;
import com.sop.backend.repositories.SurveyResultRepository;
import com.sop.backend.repositories.UserRepository;
import com.sop.backend.services.DiscussionService;
import com.sop.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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

class GetDiscussionsOfUserResultTest {

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SurveyResultRepository surveyResultRepository;

    @Mock
    private ChatRepository chatRepository;

    private DiscussionService discussionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        UserService userService = new UserService(
                userRepository,
                surveyResultRepository,
                discussionRepository,
                chatRepository
        );
        DiscussionMapper discussionMapper = new DiscussionMapper();
        discussionService = new DiscussionService(discussionRepository, discussionMapper, userService);
    }

    @Test
    void testGetDiscussionsByUser_UserHasDiscussions_ReturnsAllDiscussions() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        List<Discussion> discussions = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            discussions.add(createDiscussion("Discussion " + i, "Content " + i, i, 0));
        }

        when(discussionRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(discussions));

        Page<DiscussionDTO> result = discussionService.getDiscussionsByUser(userId, pageable);

        assertEquals(3, result.getTotalElements());
        result.getContent().forEach(discussionDTO ->
                assertTrue(discussionDTO.getTitle().contains("Discussion")));
        assertTrue(result.getContent().get(0).getContent().contains("Content"));
    }

    @Test
    void testGetDiscussionsByUser_UserHasNoDiscussions_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 20);

        when(discussionRepository.findByUserId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        Page<DiscussionDTO> result = discussionService.getDiscussionsByUser(1L, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDiscussionsByUser_MultipleDiscussions_ReturnsCorrectTitles() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        String[] titles = {"First Discussion", "Second Discussion", "Third Discussion"};
        List<Discussion> discussions = new ArrayList<>();

        for (String title : titles) {
            discussions.add(createDiscussion(title, "Content for " + title, 0, 0));
        }

        when(discussionRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(discussions));

        Page<DiscussionDTO> result = discussionService.getDiscussionsByUser(userId, pageable);

        assertEquals(titles.length, result.getTotalElements());
        for (int i = 0; i < titles.length; i++) {
            assertEquals(titles[i], result.getContent().get(i).getTitle());
        }
    }

    @Test
    void testGetDiscussionsByUser_MultipleDiscussions_ReturnsCorrectContent() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        String[] titles = {"First Discussion", "Second Discussion", "Third Discussion"};
        String[] contents = {"First content text", "Second content text", "Third content text"};
        List<Discussion> discussions = new ArrayList<>();

        for (int i = 0; i < titles.length; i++) {
            discussions.add(createDiscussion(titles[i], contents[i], 0, 0));
        }

        when(discussionRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(discussions));

        Page<DiscussionDTO> result = discussionService.getDiscussionsByUser(userId, pageable);

        assertEquals(contents.length, result.getTotalElements());
        for (int i = 0; i < contents.length; i++) {
            assertEquals(contents[i], result.getContent().get(i).getContent());
        }
    }

    @Test
    void testGetDiscussionsByUser_OneDiscussion_ReturnsOne() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        Discussion discussion = createDiscussion("Solo Discussion", "Solo Content", 5, 2);
        when(discussionRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(List.of(discussion)));

        Page<DiscussionDTO> result = discussionService.getDiscussionsByUser(userId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Solo Discussion", result.getContent().get(0).getTitle());
        assertEquals("Solo Content", result.getContent().get(0).getContent());
    }

    @Test
    void testGetDiscussionsByUser_NullUserId_ThrowsException() {
        Pageable pageable = PageRequest.of(0, 20);

        when(discussionRepository.findByUserId(null, pageable))
                .thenThrow(new IllegalArgumentException("User ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                discussionService.getDiscussionsByUser(null, pageable)
        );
    }

    @Test
    void testGetDiscussionsByUser_NegativeUserId_ThrowsException() {
        Long negativeUserId = -1L;
        Pageable pageable = PageRequest.of(0, 20);

        when(discussionRepository.findByUserId(negativeUserId, pageable))
                .thenThrow(new IllegalArgumentException("User ID must be positive"));

        assertThrows(IllegalArgumentException.class, () ->
                discussionService.getDiscussionsByUser(negativeUserId, pageable)
        );
    }

    @Test
    void testGetDiscussionsByUser_NullPageable_ThrowsException() {
        Long userId = 1L;

        when(discussionRepository.findByUserId(userId, null))
                .thenThrow(new IllegalArgumentException("Pageable cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                discussionService.getDiscussionsByUser(userId, null)
        );
    }

    @Test
    void testGetDiscussionsByUser_LargeDataSet_HandlesCorrectly() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 100);

        List<Discussion> discussions = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            discussions.add(createDiscussion("Discussion " + i, "Content " + i, 0, 0));
        }

        when(discussionRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(discussions, pageable, 1000));

        Page<DiscussionDTO> result = discussionService.getDiscussionsByUser(userId, pageable);

        assertEquals(100, result.getNumberOfElements());
        assertEquals(1000, result.getTotalElements());
        assertNotNull(result.getContent());
    }

    @Test
    void testGetDiscussionsByUser_SpecialCharacters_HandlesCorrectly() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        Discussion discussion = createDiscussion(
                "Discussion with <html> & special chars!",
                "Content with émojis 🎉 and symbols @#$%",
                0, 0
        );

        when(discussionRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(List.of(discussion)));

        Page<DiscussionDTO> result = discussionService.getDiscussionsByUser(userId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Discussion with <html> & special chars!", result.getContent().get(0).getTitle());
        assertTrue(result.getContent().get(0).getContent().contains("🎉"));
    }

    @Test
    void testGetDiscussionsByUser_VeryLongContent_HandlesCorrectly() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        String longContent = "A".repeat(10000);
        Discussion discussion = createDiscussion("Discussion", longContent, 0, 0);

        when(discussionRepository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(List.of(discussion)));

        Page<DiscussionDTO> result = discussionService.getDiscussionsByUser(userId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(10000, result.getContent().get(0).getContent().length());
    }

    private Discussion createDiscussion(String title, String content, int upvotes, int downvotes) {
        Discussion discussion = new Discussion();
        discussion.setTitle(title);
        discussion.setContent(content);
        discussion.setUpvotes(upvotes);
        discussion.setDownvotes(downvotes);

        User user = new User();
        user.setName("Test User");
        discussion.setUser(user);

        return discussion;
    }
}