package com.example.backend.dtos.out.notifications;

import com.example.backend.entities.Notification;
import com.example.backend.types.EntityType;

import java.time.Instant;

public record NotificationDto (
     Long id,
     EntityType type,
     String text,
     Instant createdAt,
     Boolean isRead
) {
    public NotificationDto(Notification notification, String text, Boolean isRead) {
        this(
            notification.getId(),
            notification.getEntityType(),
            text,
            notification.getCreatedAt(),
            isRead
        );
    }
}
