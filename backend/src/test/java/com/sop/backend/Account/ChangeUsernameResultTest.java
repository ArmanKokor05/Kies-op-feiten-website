package com.sop.backend.Account;

import com.sop.backend.models.User;
import com.sop.backend.repositories.UserRepository;
import com.sop.backend.repositories.SurveyResultRepository;
import com.sop.backend.repositories.DiscussionRepository;
import com.sop.backend.repositories.ChatRepository;
import com.sop.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChangeUsernameResultTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SurveyResultRepository surveyResultRepository;

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private ChatRepository chatRepository;

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
    void testChangeName_UserExists_NameUpdatedSuccessfully() {
        Long userId = 1L;
        String newName = "Jane";
        User user = createUser("John", "john@example.com", "password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.changeName(userId, newName);

        assertEquals("Jane", result.getName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangeName_UserNotFound_ThrowsException() {
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.changeName(userId, "Jane"));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testChangeName_OnlyNameChanges_OtherFieldsUnchanged() {
        Long userId = 1L;
        User user = createUser("John", "john@example.com", "password123");
        String originalEmail = user.getEmail();
        String originalPassword = user.getPassword();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.changeName(userId, "Jane");

        assertEquals("Jane", user.getName());
        assertEquals(originalEmail, user.getEmail());
        assertEquals(originalPassword, user.getPassword());
    }

    private User createUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }
}