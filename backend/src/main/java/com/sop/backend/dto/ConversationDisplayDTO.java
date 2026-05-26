package com.sop.backend.dto;

import java.time.LocalDateTime;

public record ConversationDisplayDTO(Long conversationId,
                                     Long userId,
                                     String conversationName,
                                     String lastMessage,
                                     LocalDateTime updatedAt) {
}
