package com.example.backend.dtos.in.apartment;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateApartmentDto (
    @NotBlank(message = "Name must be provided.")
    @Length(min = 2, max = 32, message =
            "Name length must be from 2 to 32 characters length."
    )
    String name,
    @Length(max = 64, message = "Address max length is 64 characters")
    String address
) {}
