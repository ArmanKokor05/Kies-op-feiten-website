package com.sop.backend.services;

import com.sop.backend.dto.UserInfoDTO;
import com.sop.backend.models.User;
import com.sop.backend.repositories.UserRepository;
import com.sop.backend.repositories.SurveyResultRepository;
import com.sop.backend.repositories.DiscussionRepository;
import com.sop.backend.repositories.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final DiscussionRepository discussionRepository;
    private final ChatRepository chatRepository;

    public UserService(
            UserRepository userRepository,
            SurveyResultRepository surveyResultRepository,
            DiscussionRepository discussionRepository,
            ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.surveyResultRepository = surveyResultRepository;
        this.discussionRepository = discussionRepository;
        this.chatRepository = chatRepository;
    }

    /**
     * Permanently deletes a user account from the application.
     * This is irreversible and will remove all user data
     * associated with the given user ID.
     *
     * @param userId the unique identifier of the user to delete
     * @throws IllegalArgumentException if user with given ID is not found
     *
     */
    @Transactional
    public void deleteAccount(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Delete all related data first
        surveyResultRepository.deleteByUserId(userId);
        chatRepository.deleteByUserId(userId);
        discussionRepository.deleteByUserId(userId);

        // Finally delete the user
        userRepository.deleteById(userId);
    }

    /**
     * Changes the name of a user account.
     * Updates the user's name in the database.
     *
     * @param userId the unique identifier of the user
     * @param newName the new name to set for the user
     * @return the updated User entity with the new name
     * @throws IllegalArgumentException if user with given ID is not found
     *
     */
    public User changeName(Long userId, String newName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setName(newName);
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
    }

    /**
     * Retrieves basic user information including username and creation date
     *
     * @param userId the unique identifier of the user
     * @return UserInfoDTO containing user's id, name, and creation date
     * @throws RuntimeException if user with given ID is not found
     */
    public UserInfoDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

        return new UserInfoDTO(
                user.getId(),
                user.getName(),
                user.getCreatedAt()
        );
    }

    /**
     * This method is meant for looking up users with a searchbar
     *
     * @param name can be a substring of a name
     * @return List of the top 10 users containing the (sub)string
     */
    public List<User> searchUsers(String name) {
        return userRepository.findTop10ByNameContainingIgnoreCase(name);
    }
}
