package com.sop.backend.mapper;

import com.sop.backend.dto.ChatDTO;
import com.sop.backend.models.Chat;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    /**
     * Converts a Chat entity to a ChatDTO.
     * Maps all chat fields including ID, content, vote counts,
     * creation timestamp, and user name. Also includes the discussion title
     * and ID if the chat is associated with a discussion.
     *
     * @param chat the Chat entity to convert
     * @return a ChatDTO containing the chat data
     *
     */
    public ChatDTO toDTO(Chat chat) {
        ChatDTO dto = new ChatDTO();
        dto.setId(chat.getId());
        dto.setContent(chat.getContent());
        dto.setUpvotes(chat.getUpvotes());
        dto.setDownvotes(chat.getDownvotes());
        dto.setCreated_at(chat.getCreatedAt());

        if (chat.getUser() != null) {
            dto.setUserName(chat.getUser().getName());
        }

        if (chat.getDiscussion() != null) {
            dto.setDiscussion_title(chat.getDiscussion().getTitle());
            dto.setDiscussion_id(chat.getDiscussion().getId());
        }

        return dto;
    }
}