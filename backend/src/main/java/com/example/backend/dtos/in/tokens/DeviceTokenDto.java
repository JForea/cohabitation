package com.example.backend.dtos.in.tokens;

import com.example.backend.types.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record DeviceTokenDto(
        @NotBlank
        @Length(max=255)
        String deviceId,
        @NotBlank
        @Length(max=2048)
        String token,
        @NotNull
        Platform platform
) {}
