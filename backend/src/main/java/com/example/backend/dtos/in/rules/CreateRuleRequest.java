package com.example.backend.dtos.in.rules;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateRuleRequest(
        @NotBlank @Length(max = 255)
        String text
) {}
