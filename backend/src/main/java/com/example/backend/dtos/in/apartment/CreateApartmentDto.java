package com.example.backend.dtos.in.apartment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.ZoneOffset;

public record CreateApartmentDto (
    @NotBlank(message = "Name must be provided.")
    @Length(min = 2, max = 32, message =
            "Name length must be from 2 to 32 characters length."
    )
    String name,
    @Length(max = 64, message = "Address max length is 64 characters")
    String address,
    @NotNull
    @Min(-720)
    @Max(840)
    Short minutesOffset
) {}
