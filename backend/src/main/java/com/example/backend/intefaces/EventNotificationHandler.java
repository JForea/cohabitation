package com.example.backend.intefaces;

import com.example.backend.entities.Event;
import com.example.backend.entities.User;

public interface EventNotificationHandler {
    void handleEventNotification(User createdBy, Integer apartmentId, Event event, boolean creating);
}
