package com.sop.backend.Discussions;

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

class SearchDiscussionsResultTest {

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
    void testSearchDiscussions_WithMatchingTitle_ReturnsMatchingDiscussions() {
        String searchTitle = "Spring";
        Pageable pageable = PageRequest.of(0, 20);

        List<Discussion> discussions = new ArrayList<>();
        discussions.add(createDiscussion("Spring Boot Tutorial", "Content 1", 5, 0));
        discussions.add(createDiscussion("Spring Security Guide", "Content 2", 3, 0));

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, pageable))
                .thenReturn(new PageImpl<>(discussions));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertEquals(2, result.getTotalElements());
        result.getContent().forEach(discussionDTO ->
                assertTrue(discussionDTO.getTitle().contains("Spring")));
    }

    @Test
    void testSearchDiscussions_WithNoMatches_ReturnsEmptyPage() {
        String searchTitle = "NonExistentTopic";
        Pageable pageable = PageRequest.of(0, 20);

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testSearchDiscussions_CaseInsensitiveSearch_ReturnsAllMatches() {
        String searchTitle = "java";
        Pageable pageable = PageRequest.of(0, 20);

        List<Discussion> discussions = new ArrayList<>();
        discussions.add(createDiscussion("Java Basics", "Content 1", 0, 0));
        discussions.add(createDiscussion("JAVA Advanced", "Content 2", 0, 0));
        discussions.add(createDiscussion("Introduction to java", "Content 3", 0, 0));

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, pageable))
                .thenReturn(new PageImpl<>(discussions));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertEquals(3, result.getTotalElements());
    }

    @Test
    void testSearchDiscussions_EmptySearchString_ReturnsAllDiscussions() {
        String searchTitle = "";
        Pageable pageable = PageRequest.of(0, 20);

        List<Discussion> discussions = new ArrayList<>();
        discussions.add(createDiscussion("Discussion 1", "Content 1", 0, 0));
        discussions.add(createDiscussion("Discussion 2", "Content 2", 0, 0));
        discussions.add(createDiscussion("Discussion 3", "Content 3", 0, 0));

        when(discussionRepository.findByTitleContainingIgnoreCase("", pageable))
                .thenReturn(new PageImpl<>(discussions));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertEquals(3, result.getTotalElements());
    }

    @Test
    void testSearchDiscussions_WithWhitespace_TrimsAndSearches() {
        String searchTitle = "  Spring  ";
        Pageable pageable = PageRequest.of(0, 20);

        List<Discussion> discussions = new ArrayList<>();
        discussions.add(createDiscussion("Spring Boot", "Content", 0, 0));

        when(discussionRepository.findByTitleContainingIgnoreCase("Spring", pageable))
                .thenReturn(new PageImpl<>(discussions));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertEquals(1, result.getTotalElements());
        verify(discussionRepository).findByTitleContainingIgnoreCase("Spring", pageable);
    }

    @Test
    void testSearchDiscussions_ReturnsCorrectUserNames() {
        String searchTitle = "Discussion";
        Pageable pageable = PageRequest.of(0, 20);

        Discussion discussion1 = createDiscussion("Discussion 1", "Content 1", 0, 0);
        discussion1.getUser().setName("John Doe");

        Discussion discussion2 = createDiscussion("Discussion 2", "Content 2", 0, 0);
        discussion2.getUser().setName("Jane Smith");

        List<Discussion> discussions = List.of(discussion1, discussion2);

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, pageable))
                .thenReturn(new PageImpl<>(discussions));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getUserName());
        assertEquals("Jane Smith", result.getContent().get(1).getUserName());
    }

    @Test
    void testSearchDiscussions_ReturnsCorrectUpvotesAndDownvotes() {
        String searchTitle = "Vote";
        Pageable pageable = PageRequest.of(0, 20);

        List<Discussion> discussions = new ArrayList<>();
        discussions.add(createDiscussion("Vote Discussion 1", "Content 1", 10, 2));
        discussions.add(createDiscussion("Vote Discussion 2", "Content 2", 5, 1));

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, pageable))
                .thenReturn(new PageImpl<>(discussions));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(10, result.getContent().get(0).getUpvotes());
        assertEquals(2, result.getContent().get(0).getDownvotes());
        assertEquals(5, result.getContent().get(1).getUpvotes());
        assertEquals(1, result.getContent().get(1).getDownvotes());
    }

    @Test
    void testSearchDiscussions_NullPageable_ThrowsException() {
        String searchTitle = "Test";

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, null))
                .thenThrow(new IllegalArgumentException("Pageable cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                discussionService.searchDiscussions(searchTitle, null)
        );
    }

    @Test
    void testSearchDiscussions_SpecialCharacters_HandlesCorrectly() {
        String searchTitle = "C++";
        Pageable pageable = PageRequest.of(0, 20);

        Discussion discussion = createDiscussion("C++ Programming", "Content", 0, 0);

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, pageable))
                .thenReturn(new PageImpl<>(List.of(discussion)));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getTitle().contains("C++"));
    }

    @Test
    void testSearchDiscussions_UnicodeCharacters_HandlesCorrectly() {
        String searchTitle = "日本語";
        Pageable pageable = PageRequest.of(0, 20);

        Discussion discussion = createDiscussion("日本語 Programming Tutorial", "Content", 0, 0);

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, pageable))
                .thenReturn(new PageImpl<>(List.of(discussion)));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testSearchDiscussions_VeryLongSearchTerm_HandlesCorrectly() {
        String searchTitle = "A".repeat(1000);
        Pageable pageable = PageRequest.of(0, 20);

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchDiscussions_LargeResultSet_HandlesCorrectly() {
        String searchTitle = "Tutorial";
        Pageable pageable = PageRequest.of(0, 100);

        List<Discussion> discussions = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            discussions.add(createDiscussion("Tutorial " + i, "Content " + i, 0, 0));
        }

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, pageable))
                .thenReturn(new PageImpl<>(discussions, pageable, 1000));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertEquals(100, result.getNumberOfElements());
        assertEquals(1000, result.getTotalElements());
    }

    @Test
    void testSearchDiscussions_WithNumbers_ReturnsMatches() {
        String searchTitle = "2024";
        Pageable pageable = PageRequest.of(0, 20);

        List<Discussion> discussions = List.of(
                createDiscussion("Java Tutorial 2024", "Content 1", 0, 0),
                createDiscussion("2024 Programming Trends", "Content 2", 0, 0)
        );

        when(discussionRepository.findByTitleContainingIgnoreCase(searchTitle, pageable))
                .thenReturn(new PageImpl<>(discussions));

        Page<DiscussionDTO> result = discussionService.searchDiscussions(searchTitle, pageable);

        assertEquals(2, result.getTotalElements());
        result.getContent().forEach(dto ->
                assertTrue(dto.getTitle().contains("2024")));
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