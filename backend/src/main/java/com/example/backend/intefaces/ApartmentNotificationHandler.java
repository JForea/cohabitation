package com.example.backend.intefaces;

import com.example.backend.entities.Profile;

public interface ApartmentNotificationHandler {
    void handleJoinNotification(Profile profile, boolean rejoin);
}
