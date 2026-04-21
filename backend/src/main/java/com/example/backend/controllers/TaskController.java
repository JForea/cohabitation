package com.example.backend.controllers;

import com.example.backend.dtos.in.tasks.CreateTaskDto;
import com.example.backend.dtos.out.tasks.TaskDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.TaskService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @AuthenticationPrincipal CustomUserDetails details,
            HttpServletResponse servletResponse
    ) {
        User user = details.getUser();
        Long id = taskService.create(user, apartmentId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(
            @PathVariable Integer apartmentId,
            @RequestParam(defaultValue = "0") Short page,
            @RequestParam(defaultValue = "10") Short size,
            @RequestParam(required = false) Integer assignedTo,
            @RequestParam(required = false) Boolean done,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.ok(
                taskService.getTasks(apartmentId, user, size, page, assignedTo, done)
        );
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<Boolean> changeTaskStatus(
            @PathVariable Integer apartmentId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.ok(
                taskService.switchTaskStatus(apartmentId, user, taskId)
        );
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer apartmentId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        taskService.deleteOne(apartmentId, user, taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
