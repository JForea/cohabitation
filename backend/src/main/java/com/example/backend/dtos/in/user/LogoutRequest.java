package com.example.backend.dtos.in.user;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
   @NotBlank String deviceId
) {}
