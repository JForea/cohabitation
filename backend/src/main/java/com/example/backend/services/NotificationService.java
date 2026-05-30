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
import com.example.backend.types.Role;
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
        RuleNotificationHandler,
        ProfileNotificationHandler {

    private final ProfileNotificationRepository profileNotificationRepository;

    private final INotificationTextFactory iNotificationTextFactory;

    private final NotificationRepository notificationRepository;

    private final ProfileRepository profileRepository;

    private final IPushNotificationService iPushNotificationService;

    public NotificationService(ProfileNotificationRepository profileNotificationRepository,
                               INotificationTextFactory iNotificationTextFactory,
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

    @Override
    public void handleLeaveNotification(Profile profile) {
        Notification notification = notificationRepository.save(
                new Notification(
                        profile,
                        NotificationType.USER_LEFT,
                        EntityType.USER,
                        Map.of("userName", profile.getName())
                )
        );

        broadcast(profile, notification, null);
    }

    @Override
    public void handleKickNotification(Profile actor, Profile target) {
        Notification notification = notificationRepository.save(
                new Notification(
                        actor,
                        NotificationType.USER_KICK,
                        EntityType.USER,
                        Map.of("userName", actor.getName(), "targetUserName", target.getName())
                )
        );

        broadcast(actor, notification, null);
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

    public void handleEventNotification(User createdBy, Event event, boolean creating) {
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
                        "points", task.getPoints()
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

    public void handleTaskSwitchStatus(User actor, Task task) {
        notificationRepository.save(
                new Notification(
                        actor.getCurrentProfile(),
                        task.getCompletedAt() != null ? NotificationType.TASK_DONE : NotificationType.TASK_REOPENED,
                        EntityType.TASK,
                        Map.of(
                                "taskId", task.getId(),
                                "taskName", task.getName(),
                                "userName", actor.getName(),
                                "points", task.getPoints()
                        )
                )
        );
    }

    @Override
    public void handleManyTasksDelete(User user, List<Task> tasks) {
        Profile profile = user.getCurrentProfile();
        for (Task task : tasks) {
            notificationRepository.save(
                    new Notification(
                            profile,
                            NotificationType.TASK_DELETED,
                            EntityType.TASK,
                            Map.of(
                                    "taskId", task.getId(),
                                    "taskName", task.getName(),
                                    "userName", profile.getName(),
                                    "points", task.getPoints()
                            )
                    )
            );
        }
    }

    @Override
    public void handleExpenseCreate(User createdBy, Expense expense) {
        Profile profile = createdBy.getCurrentProfile();
        Notification notification = notificationRepository.save(new Notification(
                profile,
                NotificationType.EXPENSE_CREATED,
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
    public void handleManyExpensesDelete(User createdBy, List<Expense> expenses) {
        Profile profile = createdBy.getCurrentProfile();
        List<Notification> notifications = expenses.stream().map(expense -> new Notification(
                profile,
                NotificationType.EXPENSE_DELETED,
                EntityType.EXPENSE,
                Map.of(
                        "userName", profile.getName(),
                        "expenseName", expense.getName(),
                        "expenseAmount", expense.getAmount()
                )
        )).toList();

        notificationRepository.saveAll(notifications);
    }

    @Override
    public void handleRuleUpdate(User createdBy, Rule rule, boolean creating) {
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

    @Override
    public void handleRoleChange(Profile profile, Role role) {
        NotificationType type = switch (role) {
            case Role.INHABITANT -> NotificationType.USER_INHABITANT;
            case Role.ADMIN -> NotificationType.USER_ADMIN;
            case Role.CREATOR -> NotificationType.USER_CREATOR;
        };

        Notification notification = notificationRepository.save(
                new Notification(
                        profile,
                        type,
                        EntityType.USER,
                        Map.of("userName", profile.getName())
                )
        );

        profileNotificationRepository.save(new ProfileNotification(profile, notification));
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
