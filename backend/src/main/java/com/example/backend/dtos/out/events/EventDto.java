package com.example.backend.dtos.out.events;

import com.example.backend.dtos.out.profile.ProfileBriefDto;
import com.example.backend.entities.Event;

import java.time.LocalTime;

public record EventDto(
        Long id,
        ProfileBriefDto createdBy,
        String name,
        LocalTime time,
        String description) {
    public EventDto(Event event) {
        this(
                event.getId(),
                new ProfileBriefDto(event.getCreatedBy()),
                event.getName(),
                event.getTime(),
                event.getDescription()
        );
    }
}
