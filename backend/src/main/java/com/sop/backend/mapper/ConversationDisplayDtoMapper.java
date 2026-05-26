package com.sop.backend.mapper;

import com.sop.backend.dto.ConversationDisplayDTO;
import com.sop.backend.dto.MessageDto;
import com.sop.backend.models.ConversationUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConversationDisplayDtoMapper {

    public ConversationDisplayDTO toDto(ConversationUser conversationUser, MessageDto messageDto) {
        String lastMessage = "This conversation hasn't started yet";
        LocalDateTime updatedAt = LocalDateTime.MIN;

        if (messageDto != null) {
            lastMessage = messageDto.content();
            updatedAt = messageDto.createdAt();
        }

        return new ConversationDisplayDTO(conversationUser.getConversation().getId(),
                conversationUser.getUser().getId(),
                conversationUser.getUser().getName(),
                lastMessage,
                updatedAt);
    }
}