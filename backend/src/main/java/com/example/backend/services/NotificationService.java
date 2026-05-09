package com.example.backend.services;

import com.example.backend.dtos.inner.TokenDto;
import com.example.backend.dtos.out.notifications.NotificationDto;
import com.example.backend.entities.*;
import com.example.backend.exceptions.BadRequestException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.factories.NotificationTextFactory;
import com.example.backend.intefaces.*;
import com.example.backend.repositories.NotificationRepository;
import com.example.backend.repositories.ProfileNotificationRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.types.EntityType;
import com.example.backend.types.NotificationType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class NotificationService implements
        EventNotificationHandler,
        BuyingNotificationHandler,
        ApartmentNotificationHandler,
        TaskNotificationHandler,
        ExpenseNotificationHandler,
        RuleNotificationHandler {

    private final ProfileNotificationRepository profileNotificationRepository;

    private final INotificationTextFactory iNotificationTextFactory;

    private final NotificationRepository notificationRepository;

    private final ProfileRepository profileRepository;

    private final IPushNotificationService iPushNotificationService;

    public NotificationService(ProfileNotificationRepository profileNotificationRepository,
                               NotificationTextFactory iNotificationTextFactory,
                               NotificationRepository notificationRepository,
                               ProfileRepository profileRepository,
                               IPushNotificationService iPushNotificationService) {
        this.profileNotificationRepository = profileNotificationRepository;
        this.iNotificationTextFactory = iNotificationTextFactory;
        this.notificationRepository = notificationRepository;
        this.profileRepository = profileRepository;
        this.iPushNotificationService = iPushNotificationService;
    }

    private void broadcast(Profile profile, Notification notification, NotificationType type) {
        List<Profile> neighbours = profileRepository.findAllByApartment_IdAndIdNot(
                profile.getApartment().getId(),
                profile.getId()
        );

        profileNotificationRepository.saveAll(
                neighbours.stream().map(neighbour -> new ProfileNotification(neighbour, notification)).toList()
        );

        if (type != null) {
            List<TokenDto> tokens = neighbours.stream()
                    .flatMap(neighbour ->
                            neighbour.getUser()
                                    .getDeviceTokens()
                                    .stream()
                    )
                    .map(TokenDto::new)
                    .toList();
            iPushNotificationService.send(
                    tokens,
                    iNotificationTextFactory.getTitle(type),
                    iNotificationTextFactory.getBody(
                            type,
                            notification.getPayload(),
                            true,
                            profile.getUser().getMale()
                    )
            );
        }
    }

    public void handleJoinNotification(Profile profile, boolean rejoin) {
        Notification notification = notificationRepository.save(new Notification(
                profile,
                rejoin ? NotificationType.USER_REJOINED : NotificationType.USER_JOINED,
                EntityType.USER,
                Map.of("userName", profile.getName())
        ));

        broadcast(profile, notification, null);
    }

    public void handleBuyingCreate(Profile createdBy, Profile assignedTo) {
        NotificationType type = NotificationType.BUYING_UPDATED;

        Notification notification = notificationRepository.save(new Notification(
                createdBy,
                type,
                EntityType.BUYING,
                Map.of("userName", createdBy.getName())
        ));

        if (assignedTo != null && !Objects.equals(assignedTo.getId(), createdBy.getId())) {
            profileNotificationRepository.save(new ProfileNotification(
                    assignedTo,
                    notification
            ));

            iPushNotificationService.send(
                    assignedTo.getUser().getDeviceTokens().stream().map(TokenDto::new).toList(),
                    iNotificationTextFactory.getTitle(type),
                    iNotificationTextFactory.getBody(
                            type,
                            notification.getPayload(),
                            true,
                            createdBy.getUser().getMale()
                    )
            );
        }
    }

    public void handleEventNotification(User createdBy, Integer apartmentId, Event event, boolean creating) {
        NotificationType type = creating ? NotificationType.EVENT_CREATED : NotificationType.EVENT_DELETED;
        Profile profile = createdBy.getCurrentProfile();

        Notification notification = notificationRepository.save(new Notification(
                profile,
                type,
                EntityType.EVENT,
                Map.of("eventId", event.getId(),
                        "userName", profile.getName(),
                        "eventName", event.getName()
                )
        ));

        broadcast(createdBy.getCurrentProfile(), notification, type);
    }

    public void handleTaskCreate(User createdBy, Profile assignedTo, Task task) {
        Profile creatorProfile = createdBy.getCurrentProfile();

        Notification notification = new Notification(
                creatorProfile,
                NotificationType.TASK_CREATED,
                EntityType.TASK,
                Map.of(
                        "taskId", task.getId(),
                        "taskName", task.getName(),
                        "userName", creatorProfile.getName(),
                        "point", task.getPoints()
                )
        );

        notification = notificationRepository.save(notification);

        if (assignedTo != null &&
                !Objects.equals(assignedTo.getId(), creatorProfile.getId())) {
            ProfileNotification profileNotification = new ProfileNotification(
                    assignedTo,
                    notification
            );

            profileNotificationRepository.save(profileNotification);

            iPushNotificationService.send(
                    assignedTo.getUser().getDeviceTokens()
                            .stream().map(TokenDto::new)
                            .toList(),
                    iNotificationTextFactory.getTitle(NotificationType.TASK_CREATED),
                    iNotificationTextFactory.getBody(
                            NotificationType.TASK_CREATED,
                            notification.getPayload(),
                            true,
                            createdBy.getMale()
                    )
            );
        }
    }

    public void handleSwitchStatus(User actor, Task task) {
        notificationRepository.save(
                new Notification(
                        actor.getCurrentProfile(),
                        task.getCompletedAt() != null ? NotificationType.TASK_DONE : NotificationType.TASK_REOPENED,
                        EntityType.TASK,
                        Map.of(
                                "taskId", task.getId(),
                                "taskName", task.getName(),
                                "userName", actor.getName(),
                                "point", task.getPoints()
                        )
                )
        );
    }

    @Override
    public void handleExpenseNotification(User createdBy, Expense expense, boolean creating) {
        Profile profile = createdBy.getCurrentProfile();
        Notification notification = notificationRepository.save(new Notification(
                profile,
                creating ? NotificationType.EXPENSE_CREATED : NotificationType.EXPENSE_DELETED,
                EntityType.EXPENSE,
                Map.of(
                        "userName", profile.getName(),
                        "expenseName", expense.getName(),
                        "expenseAmount", expense.getAmount()
                )
        ));

        broadcast(profile, notification, null);
    }

    @Override
    public void handleRuleNotification(User createdBy, Rule rule, boolean creating) {
        Profile profile = createdBy.getCurrentProfile();
        Notification notification = notificationRepository.save(new Notification(
                profile,
                creating ? NotificationType.RULE_CREATED : NotificationType.RULE_DELETED,
                EntityType.RULE,
                Map.of(
                        "userName", profile.getName(),
                        "ruleText", rule.getText()
                )
        ));

        broadcast(profile, notification, null);
    }

    public Integer getUnreadCount(User user) {
        return profileNotificationRepository.countByKey_ProfileAndIsReadFalse(user.getCurrentProfile());
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
                    iNotificationTextFactory.getBody(
                            notification.getType(),
                            notification.getPayload(),
                            true,
                            notification.getActor().getUser().getMale()
                    ),
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
