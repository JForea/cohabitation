package com.example.backend.dtos.inner;

import com.example.backend.entities.DeviceToken;

public record TokenDto(
        Integer id,
        String token
) {
    public TokenDto(DeviceToken deviceToken) {
        this(
                deviceToken.getId(),
                deviceToken.getToken()
        );
    }
}
