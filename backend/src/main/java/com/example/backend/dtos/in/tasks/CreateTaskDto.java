package com.example.backend.dtos.in.tasks;

import com.example.backend.types.Room;
import com.example.backend.types.TaskPriority;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateTaskDto(
        @NotBlank(message = "Name must not be blank.")
        @Size(min = 3, max = 64, message = "Name length must be from 3 to 64 characters")
        String name,
        @Size(max = 256, message = "Description length must be up to 256 characters.")
        String description,
        @Min(1)
        Long assignedTo,
        Room room,
        TaskPriority priority,
        @Min(0)
        @Max(100)
        Short points,
        @Min(1)
        @Max(365)
        Short repeatTime,
        @FutureOrPresent
        LocalDate dueDate
) {}