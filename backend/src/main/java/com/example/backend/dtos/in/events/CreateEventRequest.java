package com.example.backend.dtos.in.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateEventRequest(
        @NotBlank
        @Length(max = 64)
        String name,
        @NotNull
        LocalDate date,
        LocalTime time,
        @Length(max = 256)
        String description
) {}
