package com.example.backend.controllers;

import com.example.backend.dtos.in.tasks.CreateTaskDto;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.common.StatusResponse;
import com.example.backend.dtos.out.tasks.TaskDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("@apartmentSecurity.hasAccess(#apartmentId, authentication)")
@RequestMapping("/api/apartments/{apartmentId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<IdResponse<Long>> create(
            @RequestBody @Valid CreateTaskDto dto,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(user, dto));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(
            @PathVariable Integer apartmentId,
            @RequestParam(defaultValue = "0") Short page,
            @RequestParam(defaultValue = "10") Short size,
            @RequestParam(required = false) Integer assignedTo,
            @RequestParam(required = false) Boolean done
    ) {
        return ResponseEntity.ok(
                taskService.getTasks(apartmentId, size, page, assignedTo, done)
        );
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<StatusResponse> changeTaskStatus(
            @PathVariable Long taskId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.ok(
                taskService.switchTaskStatus(user, taskId)
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
