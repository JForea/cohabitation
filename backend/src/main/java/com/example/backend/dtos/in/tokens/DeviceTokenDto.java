package com.example.backend.dtos.in.tokens;

import com.example.backend.types.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeviceTokenDto(
        @NotBlank
        @Size(max=255)
        String deviceId,
        @NotBlank
        @Size(max=2048)
        String token,
        @NotNull
        Platform platform
) {}
