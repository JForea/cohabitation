package com.example.backend.dtos.out.tasks;

import com.example.backend.dtos.out.profile.ProfileBriefDto;
import com.example.backend.entities.Task;

public record CreateTaskResponse (
    Long id,
    ProfileBriefDto assignedTo
) {
    public CreateTaskResponse(Task task) {
        this(
                task.getId(),
                task.getAssignedTo() != null ? new ProfileBriefDto(task.getAssignedTo()) : null
        );
    }
}
