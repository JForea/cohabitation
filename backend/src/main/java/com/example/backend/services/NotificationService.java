package com.example.backend.services;

import com.example.backend.dtos.out.notifications.NotificationDto;
import com.example.backend.entities.Notification;
import com.example.backend.entities.ProfileNotification;
import com.example.backend.entities.User;
import com.example.backend.exceptions.BadRequestException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.factories.NotificationTextFactory;
import com.example.backend.intefaces.INotificationTextFactory;
import com.example.backend.repositories.ProfileNotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final ProfileNotificationRepository profileNotificationRepository;

    private final INotificationTextFactory notificationTextFactory;

    public NotificationService(ProfileNotificationRepository profileNotificationRepository,
                               NotificationTextFactory notificationTextFactory) {
        this.profileNotificationRepository = profileNotificationRepository;
        this.notificationTextFactory = notificationTextFactory;
    }

    public Integer getUnreadCount(User user) {
        return profileNotificationRepository.getCountUnreadByProfile(user.getCurrentProfile());
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
                    notificationTextFactory.getBody(notification.getType(), notification.getPayload(), true),
                    profileNotification.getRead()
            );
        }).toList();
    }

    public void markAsRead(Long notificationId, User user) {
        ProfileNotification profileNotification = profileNotificationRepository.findByKey_ProfileAndKey_Notification_Id(
                user.getCurrentProfile(),
                notificationId
        ).orElseThrow(() -> new ResourceNotFoundException("Notification not found."));

        if (profileNotification.getRead() == true)
            throw new BadRequestException("Notification is already read.");

        profileNotification.setRead(true);

        profileNotificationRepository.save(profileNotification);
    }

    public void markAsReadAll(User user) {
        List<ProfileNotification> profileNotifications = profileNotificationRepository
                .findAllByKey_ProfileAndIsReadFalse(user.getCurrentProfile());

        profileNotifications.forEach(n -> n.setRead(true));

        profileNotificationRepository.saveAll(profileNotifications);
    }
}
