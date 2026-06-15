package com.example.backend.intefaces;

import com.example.backend.entities.Profile;
import com.example.backend.entities.Task;
import com.example.backend.entities.User;

import java.util.List;

public interface TaskNotificationHandler {
    void handleTaskCreate(User createdBy, Profile assignedTo, Task task);
    void handleTaskSwitchStatus(User actor, Task task);
    void handleManyTasksDelete(User user, List<Task> tasks);
}
