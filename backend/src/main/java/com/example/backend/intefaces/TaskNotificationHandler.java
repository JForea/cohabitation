package com.example.backend.intefaces;

import com.example.backend.entities.Profile;
import com.example.backend.entities.Task;
import com.example.backend.entities.User;

public interface TaskNotificationHandler {
    void handleTaskCreate(User createdBy, Profile assignedTo, Task task);
    void handleSwitchStatus(User actor, Task task);
}
