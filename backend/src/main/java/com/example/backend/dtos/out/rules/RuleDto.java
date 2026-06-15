package com.example.backend.dtos.out.rules;

import com.example.backend.entities.Rule;

public record RuleDto(
        Long id,
        String text
) {
    public RuleDto(Rule rule) {
        this(
                rule.getId(),
                rule.getText()
        );
    }
}
