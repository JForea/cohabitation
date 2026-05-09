package com.example.backend.intefaces;

import com.example.backend.types.NotificationType;

import java.util.Map;

public interface INotificationTextFactory {
    String getTitle(NotificationType type);
    String getBody(NotificationType type, Map<String, Object> payload, boolean isPersonal);
}
