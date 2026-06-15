package com.example.backend.controllers;

import com.example.backend.dtos.out.notifications.NotificationDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("@apartmentSecurity.hasAccess(#apartmentId, authentication)")
@RequestMapping("/api/apartments/{apartmentId}/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.ok(notificationService.getUnreadCount(user));
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> get(
            @RequestParam(defaultValue = "0") Short page,
            @RequestParam(defaultValue = "10") Short size,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.ok(notificationService.findPersonal(user, page, size));
    }

    @PatchMapping
    public ResponseEntity<Void> readAll(
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        notificationService.markAsReadAll(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> read(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        notificationService.markAsRead(notificationId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
