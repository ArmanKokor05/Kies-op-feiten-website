package com.sop.backend.dto;

public record ConversationDTO (Long conversationId,
                               Long userId,
                               String conversationName) {
}
