package com.example.backend.controllers;

import com.example.backend.dtos.out.notifications.NotificationDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.NotificationService;
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

    @GetMapping
    public ResponseEntity<List<NotificationDto>> get(
            @RequestParam(defaultValue = "0") Short page,
            @RequestParam(defaultValue = "10") Short size,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.ok(notificationService.findPersonal(user, page, size));
    }
}
