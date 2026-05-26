package com.sop.backend.mapper;

import com.sop.backend.dto.ConversationDTO;
import com.sop.backend.models.User;
import org.springframework.stereotype.Component;

@Component
public class ConversationDtoMapper {

    public ConversationDTO toDto(long conversationId, User user) {
        return new ConversationDTO(conversationId, user.getId(), user.getName());
    }
}
