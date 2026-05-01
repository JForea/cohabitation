package com.example.backend.dtos.out.tasks;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.Task;
import com.example.backend.types.Room;
import com.example.backend.types.TaskPriority;

import java.time.LocalDate;

public record TaskDto(
        Long id,
        ProfileDto createdBy,
        ProfileDto assignedTo,
        ProfileDto completedBy,
        String name,
        String description,
        Room room,
        TaskPriority priority,
        Short points,
        LocalDate dueTime
) {
    public TaskDto(Task task) {
        this(
                task.getId(),
                new ProfileDto(task.getCreatedBy()),
                task.getAssignedTo() != null ? new ProfileDto(task.getAssignedTo()) : null,
                task.getCompletedBy() != null ? new ProfileDto(task.getCompletedBy()) : null,
                task.getName(),
                task.getDescription(),
                task.getRoom(),
                task.getPriority(),
                task.getPoints(),
                task.getDueTime()
        );
    }
}
