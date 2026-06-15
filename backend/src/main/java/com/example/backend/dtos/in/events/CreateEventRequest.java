package com.example.backend.dtos.in.events;

import com.example.backend.validation.annotations.ValidEventDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

@ValidEventDateTime
public record CreateEventRequest(
        @NotBlank
        @Size(max = 64)
        String name,
        @NotNull
        LocalDate date,
        LocalTime time,
        @Size(max = 256)
        String description
) {}
