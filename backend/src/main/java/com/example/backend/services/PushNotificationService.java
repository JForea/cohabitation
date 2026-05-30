package com.example.backend.services;

import com.example.backend.dtos.inner.TokenDto;
import com.example.backend.intefaces.IPushNotificationService;
import com.example.backend.repositories.DeviceTokenRepository;
import com.google.firebase.messaging.*;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PushNotificationService implements IPushNotificationService {

    private final DeviceTokenRepository deviceTokenRepository;

    private final FirebaseMessaging firebaseMessaging;

    public PushNotificationService(DeviceTokenRepository deviceTokenRepository,
                                   FirebaseMessaging firebaseMessaging) {
        this.deviceTokenRepository = deviceTokenRepository;
        this.firebaseMessaging = firebaseMessaging;
    }

    private boolean isInvalidToken(FirebaseMessagingException e) {

        MessagingErrorCode code = e.getMessagingErrorCode();

        return code == MessagingErrorCode.UNREGISTERED
                || code == MessagingErrorCode.INVALID_ARGUMENT;
    }

    @Async("pushExecutor")
    @Transactional
    public void send(
            List<TokenDto> tokenDtos,
            String title,
            String body
    ) {
        List<Message> messages = tokenDtos.stream().map(
                dto -> Message.builder()
                        .setToken(dto.token())
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                        ).build())
                .toList();

        try {
            BatchResponse batchResponse = firebaseMessaging.sendEach(messages);

            if (batchResponse.getFailureCount() == 0)
                return;

            List<SendResponse> responses = batchResponse.getResponses();
            List<Integer> badTokenDeviceIds = new ArrayList<>();

            for (int i = 0; i < responses.size(); i++) {
                SendResponse response = responses.get(i);

                FirebaseMessagingException e = response.getException();

                if (e != null && isInvalidToken(e))
                    badTokenDeviceIds.add(tokenDtos.get(i).id());
            }

            if (!badTokenDeviceIds.isEmpty())
                deleteInvalidDevices(badTokenDeviceIds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    private void deleteInvalidDevices(List<Integer> badTokenDeviceIds) {
        deviceTokenRepository.deleteByIdIn(badTokenDeviceIds);
    }
}
