package com.example.backend.dtos.in.rules;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRuleRequest(
        @NotBlank @Size(max = 255)
        String text
) {}
