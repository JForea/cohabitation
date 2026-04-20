package com.example.backend.dtos.out.common;

public record ErrorDetails(
        String type,
        String message,
        Integer code
) {}
