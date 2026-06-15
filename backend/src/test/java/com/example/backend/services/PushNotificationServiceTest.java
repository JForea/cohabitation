package com.example.backend.services;

import com.example.backend.dtos.inner.TokenDto;
import com.example.backend.repositories.DeviceTokenRepository;
import com.google.firebase.messaging.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PushNotificationServiceTest {

    @Mock
    private DeviceTokenRepository deviceTokenRepository;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private PushNotificationService pushNotificationService;

    @Test
    void shouldSendNotificationsSuccessfully() throws Exception {
        TokenDto dto = new TokenDto(1, "token");

        BatchResponse batchResponse = mock(BatchResponse.class);

        when(firebaseMessaging.sendEach(anyList()))
                .thenReturn(batchResponse);

        when(batchResponse.getFailureCount())
                .thenReturn(0);

        pushNotificationService.send(
                List.of(dto),
                "title",
                "body"
        );

        verify(firebaseMessaging)
                .sendEach(anyList());

        verifyNoInteractions(deviceTokenRepository);
    }

    @Test
    void shouldDeleteInvalidTokens() throws Exception {
        TokenDto dto1 = new TokenDto(1, "badToken");
        TokenDto dto2 = new TokenDto(2, "okToken");

        BatchResponse batchResponse = mock(BatchResponse.class);

        SendResponse badResponse = mock(SendResponse.class);
        SendResponse goodResponse = mock(SendResponse.class);

        FirebaseMessagingException exception =
                mock(FirebaseMessagingException.class);

        when(exception.getMessagingErrorCode())
                .thenReturn(MessagingErrorCode.UNREGISTERED);

        when(badResponse.getException())
                .thenReturn(exception);

        when(goodResponse.getException())
                .thenReturn(null);

        when(batchResponse.getFailureCount())
                .thenReturn(1);

        when(batchResponse.getResponses())
                .thenReturn(List.of(badResponse, goodResponse));

        when(firebaseMessaging.sendEach(anyList()))
                .thenReturn(batchResponse);

        pushNotificationService.send(
                List.of(dto1, dto2),
                "title",
                "body"
        );

        verify(deviceTokenRepository)
                .deleteByIdIn(List.of(1));
    }

    @Test
    void shouldNotDeleteTokensForOtherErrors() throws Exception {
        TokenDto dto = new TokenDto(1, "token");

        BatchResponse batchResponse = mock(BatchResponse.class);

        SendResponse response = mock(SendResponse.class);

        FirebaseMessagingException exception =
                mock(FirebaseMessagingException.class);

        when(exception.getMessagingErrorCode())
                .thenReturn(MessagingErrorCode.INTERNAL);

        when(response.getException())
                .thenReturn(exception);

        when(batchResponse.getFailureCount())
                .thenReturn(1);

        when(batchResponse.getResponses())
                .thenReturn(List.of(response));

        when(firebaseMessaging.sendEach(anyList()))
                .thenReturn(batchResponse);

        pushNotificationService.send(
                List.of(dto),
                "title",
                "body"
        );

        verifyNoInteractions(deviceTokenRepository);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFirebaseFails() throws Exception {
        TokenDto dto = new TokenDto(1, "token");

        when(firebaseMessaging.sendEach(anyList()))
                .thenThrow(new RuntimeException("firebase failed"));

        assertThrows(RuntimeException.class, () ->
                pushNotificationService.send(
                        List.of(dto),
                        "title",
                        "body"
                )
        );
    }

}
