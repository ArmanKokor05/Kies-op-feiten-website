package com.sop.backend.dto;

import java.time.LocalDateTime;

public record MessageDto(Long messageId,
                         Long conversationId,
                         Long senderId,
                         Long receiverId,
                         String content,
                         LocalDateTime createdAt) {
}
