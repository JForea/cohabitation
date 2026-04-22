package com.example.backend.dtos.in.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record AuthenticationDto(
    @Email(message = "Email is invalid.")
    @NotBlank(message = "Email must be provided.")
    String email,
    @NotBlank(message = "Password must be provided.")
    @Length(min = 8, max = 32, message =
            "Password length must be 8-32 characters length.")
    String password
) {}
