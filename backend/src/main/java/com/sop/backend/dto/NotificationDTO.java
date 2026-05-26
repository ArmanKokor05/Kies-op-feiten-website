package com.sop.backend.dto;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long id,
        LocalDateTime createdAt,
        String title,
        String message,
        Boolean read,
        String data,
        String actionUrl
) {}