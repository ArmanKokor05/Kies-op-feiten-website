package com.sop.backend.Account;

import com.sop.backend.models.User;
import com.sop.backend.repositories.ChatRepository;
import com.sop.backend.repositories.DiscussionRepository;
import com.sop.backend.repositories.SurveyResultRepository;
import com.sop.backend.repositories.UserRepository;
import com.sop.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteAccountResultTest {

    @Mock
    private SurveyResultRepository surveyResultRepository;

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(
                userRepository,
                surveyResultRepository,
                discussionRepository,
                chatRepository
        );
    }

    @Test
    void testDeleteAccount_UserExists_DeletesSuccessfully() {
        Long userId = 1L;
        User user = createUser("John", "john@example.com", "password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteAccount(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteAccount_UserNotFound_ThrowsException() {
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.deleteAccount(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(any());
    }

    private User createUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }
}