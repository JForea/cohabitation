package com.example.backend.dtos.out.tasks;

import com.example.backend.dtos.out.profile.ProfileBriefDto;
import com.example.backend.entities.Task;
import com.example.backend.types.Room;
import com.example.backend.types.TaskPriority;

import java.time.LocalDate;

public record TaskDto(
        Long id,
        ProfileBriefDto createdBy,
        ProfileBriefDto assignedTo,
        ProfileBriefDto completedBy,
        String name,
        String description,
        Room room,
        TaskPriority priority,
        Short points,
        LocalDate dueDate
) {
    public TaskDto(Task task) {
        this(
                task.getId(),
                new ProfileBriefDto(task.getCreatedBy()),
                task.getAssignedTo() != null ? new ProfileBriefDto(task.getAssignedTo()) : null,
                task.getCompletedBy() != null ? new ProfileBriefDto(task.getCompletedBy()) : null,
                task.getName(),
                task.getDescription(),
                task.getRoom(),
                task.getPriority(),
                task.getPoints(),
                task.getDueTime()
        );
    }
}
