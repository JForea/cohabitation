package com.example.backend.services;

import com.example.backend.dtos.out.notifications.NotificationDto;
import com.example.backend.entities.Notification;
import com.example.backend.entities.ProfileNotification;
import com.example.backend.entities.User;
import com.example.backend.entities.keys.ProfileNotificationKey;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.repositories.NotificationRepository;
import com.example.backend.repositories.ProfileNotificationRepository;
import com.example.backend.types.NotificationType;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final ProfileNotificationRepository profileNotificationRepository;


    public NotificationService(NotificationRepository notificationRepository,
                               ProfileNotificationRepository profileNotificationRepository) {
        this.notificationRepository = notificationRepository;
        this.profileNotificationRepository = profileNotificationRepository;
    }

    private String getText(NotificationType type, Map<String, Object> payload, boolean isPersonal) {
        switch (type) {
            case TASK_CREATED:
                String template;
                if (isPersonal)
                    template = "%s назначил вам задачу «%s».";
                else
                    template = "%s создал задачу «%s».";

                return template.formatted(payload.get("userName"), payload.get("taskName"));

            case TASK_DONE:
                return "%s завершил задачу «%s»".formatted(payload.get("userName"), payload.get("taskName"));

            case TASK_REOPENED:
                return "%s отметил задачу «%s» незавершённой".formatted(payload.get("userName"), payload.get("taskName"));
        }

        throw new NotImplementedException("Unknown notification type met during preparing notification text.");
    }

    public List<NotificationDto> findPersonal(User user, Short page, Short size) {
        return profileNotificationRepository.findAllByKey_Profile(
                user.getCurrentProfile(),
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Order.desc("key.notification.createdAt"),
                                Sort.Order.desc("key.notification.id")
                        )
                )
        ).stream().map(profileNotification -> {
            Notification notification = profileNotification.getKey().getNotification();

            return new NotificationDto(
                    notification,
                    getText(notification.getType(), notification.getPayload(), true),
                    profileNotification.getRead()
            );
        }).toList();
    }
}
