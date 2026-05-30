package com.example.backend.dtos.in.user;

import com.example.backend.dtos.in.tokens.DeviceTokenDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record RegisterDto(
        @Length(max = 255)
        @Email(message = "Email is invalid.")
        @NotBlank(message = "Email must be provided.")
        String email,
        @NotBlank(message = "Password must be provided.")
        @Length(min = 8, max = 32, message = "Password length must be 8-32 characters length.")
        String password,
        @NotBlank(message = "Name must be provided.")
        @Length(max = 40, message = "Name length must be up to 40 characters long.")
        String name,
        @NotNull(message = "Gender must be provided.")
        Boolean male,
        DeviceTokenDto deviceToken
) {}