package com.example.backend.controllers;

import com.example.backend.dtos.in.events.CreateEventRequest;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.events.EventDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

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

    @GetMapping("/calendar")
    public ResponseEntity<List<LocalDate>> getCalendar(
            @PathVariable Integer apartmentId,
            @RequestParam Integer year,
            @RequestParam Month month
            ) {
        return ResponseEntity.ok(eventService.getEventDates(apartmentId, year, month));
    }

    @GetMapping("/day/{date}")
    public ResponseEntity<List<EventDto>> getEventsByDay(
            @PathVariable Integer apartmentId,
            @PathVariable LocalDate date
            ) {
        return ResponseEntity.ok(eventService.getEventsByDay(apartmentId, date));
    }
}
