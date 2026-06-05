package com.example.backend.dtos.in.apartment;

import jakarta.validation.constraints.*;

public record CreateApartmentDto (
    @NotBlank(message = "Name must be provided.")
    @Size(min = 2, max = 32, message =
            "Name length must be from 2 to 32 characters length."
    )
    String name,
    @Size(max = 64, message = "Address max length is 64 characters")
    String address,
    @NotNull
    @Min(-720)
    @Max(840)
    Short minutesOffset
) {}
