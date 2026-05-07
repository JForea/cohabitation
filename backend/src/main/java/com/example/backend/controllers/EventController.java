package com.example.backend.controllers;

import com.example.backend.dtos.in.events.CreateEventRequest;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("@apartmentSecurity.hasAccess(#apartmentId, authentication)")
@RequestMapping("/api/apartments/{apartmentId}/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<IdResponse<Long>> create(
            @RequestBody CreateEventRequest dto,
            @AuthenticationPrincipal CustomUserDetails details
            ) {
        User user = details.getUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(user, dto));
    }
}
