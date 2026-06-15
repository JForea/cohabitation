package com.example.backend.intefaces;

import com.example.backend.entities.Profile;

public interface BuyingNotificationHandler {
    void handleBuyingCreate(Profile createdBy, Profile assignedTo);
}
