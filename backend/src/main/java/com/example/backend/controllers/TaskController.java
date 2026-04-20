package com.example.backend.controllers;

import com.example.backend.dtos.in.tasks.CreateTaskDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{apartmentId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Long> create(
            @RequestBody CreateTaskDto dto,
            @PathVariable Integer apartmentId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        Long id = taskService.create(user, apartmentId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
}
