package com.sop.backend.services;

import com.sop.backend.dto.ChatDTO;
import com.sop.backend.exceptions.ChatException;
import com.sop.backend.mapper.ChatMapper;
import com.sop.backend.models.Chat;
import com.sop.backend.models.Discussion;
import com.sop.backend.models.User;
import com.sop.backend.repositories.ChatRepository;
import com.sop.backend.repositories.DiscussionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final UserService userService;
    private final DiscussionRepository discussionRepository;
    private final NotificationService notificationService;

    public ChatService(ChatRepository chatRepository,
                       ChatMapper chatMapper,
                       UserService userService,
                       DiscussionRepository discussionRepository, NotificationService notificationService) {
        this.chatRepository = chatRepository;
        this.chatMapper = chatMapper;
        this.userService = userService;
        this.discussionRepository = discussionRepository;
        this.notificationService = notificationService;
    }

    public ChatDTO createChat(ChatDTO chatDTO, Long userId) {

        if (chatDTO.getContent() == null || chatDTO.getContent().trim().isEmpty()) {
            throw new ChatException("Content is empty");
        }

        String trimmedContent = chatDTO.getContent().trim();

        if (chatDTO.getDiscussion_id() == null) {
            throw new ChatException("Discussion ID is required");
        }

        User user = userService.findById(userId);

        Discussion discussion = discussionRepository.findById(chatDTO.getDiscussion_id())
                .orElseThrow(() -> {
                    logger.error("Discussion not found with ID: {}", chatDTO.getDiscussion_id());
                    return new ChatException("Discussion not found");
                });


        Chat chat = new Chat();
        chat.setContent(trimmedContent);
        chat.setUser(user);
        chat.setDiscussion(discussion);

        chat = this.chatRepository.save(chat);

        if (!discussion.getUser().getId().equals(userId)) {
            String notificationTitle = "New Chat";
            String notificationMessage = user.getName() + " replied to your discussion: " + discussion.getTitle();
            String urlToChat = "/discussies/" + discussion.getId() + "#chat-" + chat.getId();
            String metadata = String.format("{\"chatId\":%d,\"discussionId\":%d}",
                    chat.getId(), discussion.getId());

            notificationService.createNotificationWithData(
                    discussion.getUser(),
                    notificationTitle,
                    notificationMessage,
                    urlToChat,
                    metadata
            );
        }

        chatDTO.setId(chat.getId());
        chatDTO.setCreated_at(chat.getCreatedAt());

        return chatDTO;
    }

    /**
     * Retrieves paginated chats/responses created by a specific user.
     *
     * @param userId the unique identifier of the user
     * @param pageable the pagination information (page number, size, sort)
     * @return a page of chats/responses DTOs created by the user
     *
     * @see ChatRepository#findByUserId(Long, Pageable)
     */
    public Page<ChatDTO> getChatsByUser(Long userId, Pageable pageable) {
        return chatRepository.findByUserId(userId, pageable)
                .map(chatMapper::toDTO);
    }

    /**
     * Retrieves paginated chats/responses for a specific discussion.
     *
     * @param discussionId the unique identifier of the discussion
     * @param pageable the pagination information (page number, size, sort)
     * @return a page of chats/responses DTOs for the discussion
     *
     * @see ChatRepository#findByDiscussionId(Long, Pageable)
     */
    public Page<ChatDTO> getChatsByDiscussion(Long discussionId, Pageable pageable) {
        logger.info("Fetching chats for discussion: {}", discussionId);
        return chatRepository.findByDiscussionId(discussionId, pageable)
                .map(chatMapper::toDTO);
    }
}