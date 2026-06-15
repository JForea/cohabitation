package com.example.backend.intefaces;

import com.example.backend.entities.Profile;
import com.example.backend.types.Role;

public interface ProfileNotificationHandler {
    void handleKickNotification(Profile actor, Profile target);
    void handleRoleChange(Profile profile, Role role);
}
