package com.example.backend.dtos.in.user;

import com.example.backend.dtos.in.tokens.DeviceTokenDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationDto(
        @Size(max = 255)
        @Email(message = "Email is invalid.")
        @NotBlank(message = "Email must be provided.")
        String email,
        @NotBlank(message = "Password must be provided.")
        @Size(min = 8, max = 32, message = "Password length must be 8-32 characters length.")
        String password,
        DeviceTokenDto deviceToken
) {}
