package com.example.backend.intefaces;

import com.example.backend.dtos.inner.TokenDto;

import java.util.List;

public interface IPushNotificationService {
    void send(List<TokenDto> tokenDtos, String title, String body);
}
