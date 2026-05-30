package com.example.backend.services;

import com.example.backend.dtos.out.notifications.NotificationDto;
import com.example.backend.entities.*;
import com.example.backend.entities.keys.ProfileNotificationKey;
import com.example.backend.exceptions.BadRequestException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.intefaces.INotificationTextFactory;
import com.example.backend.intefaces.IPushNotificationService;
import com.example.backend.repositories.NotificationRepository;
import com.example.backend.repositories.ProfileNotificationRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.types.EntityType;
import com.example.backend.types.NotificationType;
import com.example.backend.types.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private ProfileNotificationRepository profileNotificationRepository;

    @Mock
    private INotificationTextFactory iNotificationTextFactory;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private IPushNotificationService iPushNotificationService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldHandleJoinNotificationWithoutPush() {
        Profile profile = mock(Profile.class);
        Apartment apartment = mock(Apartment.class);

        Notification notification = mock(Notification.class);

        when(profile.getName()).thenReturn("Марк");
        when(profile.getId()).thenReturn(1L);
        when(profile.getApartment()).thenReturn(apartment);
        when(apartment.getId()).thenReturn(10);

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notification);

        when(profileRepository.findAllByApartment_IdAndIdNot(10, 1L))
                .thenReturn(List.of(mock(Profile.class)));

        notificationService.handleJoinNotification(profile, false);

        verify(notificationRepository).save(any(Notification.class));
        verify(profileNotificationRepository).saveAll(anyList());
        verifyNoInteractions(iPushNotificationService);
    }

    @Test
    void shouldHandleLeaveNotification() {
        Profile profile = mock(Profile.class);
        Apartment apartment = mock(Apartment.class);

        Notification notification = mock(Notification.class);

        when(profile.getId()).thenReturn(1L);
        when(profile.getName()).thenReturn("Марк");
        when(profile.getApartment()).thenReturn(apartment);
        when(apartment.getId()).thenReturn(10);

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notification);

        when(profileRepository.findAllByApartment_IdAndIdNot(10, 1L))
                .thenReturn(List.of(mock(Profile.class)));

        notificationService.handleLeaveNotification(profile);

        verify(notificationRepository).save(any(Notification.class));
        verify(profileNotificationRepository).saveAll(anyList());
        verifyNoInteractions(iPushNotificationService);
    }

    @Test
    void shouldHandleKickNotification() {
        Profile actor = mock(Profile.class);
        Profile target = mock(Profile.class);
        Apartment apartment = mock(Apartment.class);

        Notification notification = mock(Notification.class);

        when(actor.getId()).thenReturn(1L);
        when(actor.getName()).thenReturn("Марк");
        when(actor.getApartment()).thenReturn(apartment);
        when(apartment.getId()).thenReturn(10);

        when(target.getName()).thenReturn("Анна");

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notification);

        when(profileRepository.findAllByApartment_IdAndIdNot(10, 1L))
                .thenReturn(List.of(mock(Profile.class)));

        notificationService.handleKickNotification(actor, target);

        verify(notificationRepository).save(any(Notification.class));
        verify(profileNotificationRepository).saveAll(anyList());
        verifyNoInteractions(iPushNotificationService);
    }

    @Test
    void shouldNotSendPushOnBuyingCreateWhenAssignedToIsCreator() {
        Profile createdBy = mock(Profile.class);
        Profile assignedTo = mock(Profile.class);

        when(createdBy.getId()).thenReturn(1L);
        when(createdBy.getName()).thenReturn("Марк");

        when(assignedTo.getId()).thenReturn(1L);

        when(notificationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        notificationService.handleBuyingCreate(createdBy, assignedTo);

        verify(notificationRepository).save(any(Notification.class));
        verifyNoInteractions(profileNotificationRepository);
        verifyNoInteractions(iPushNotificationService);
    }

    @Test
    void shouldSendNotificationOnBuyingCreateAndPushIfDifferentUsers() {
        Profile createdBy = mock(Profile.class);
        Profile assignedTo = mock(Profile.class);
        User userCreator = mock(User.class);
        User assignedUser = mock(User.class);
        DeviceToken token1 = mock(DeviceToken.class);
        DeviceToken token2 = mock(DeviceToken.class);

        when(assignedTo.getUser()).thenReturn(assignedUser);

        when(createdBy.getId()).thenReturn(1L);
        when(assignedTo.getId()).thenReturn(2L);

        when(createdBy.getName()).thenReturn("Марк");

        when(createdBy.getUser()).thenReturn(userCreator);
        when(userCreator.getMale()).thenReturn(true);
        when(assignedUser.getDeviceTokens()).thenReturn(Set.of(token1, token2));

        Notification notification = new Notification(
                createdBy,
                NotificationType.BUYING_UPDATED,
                EntityType.BUYING,
                Map.of("userName", "Марк")
        );

        when(notificationRepository.save(any())).thenReturn(notification);

        when(iNotificationTextFactory.getTitle(any())).thenReturn("title");
        when(iNotificationTextFactory.getBody(any(), any(), anyBoolean(), anyBoolean()))
                .thenReturn("body");

        notificationService.handleBuyingCreate(createdBy, assignedTo);

        verify(notificationRepository).save(any(Notification.class));
        verify(profileNotificationRepository).save(any(ProfileNotification.class));

        verify(iPushNotificationService).send(
                anyList(),
                eq("title"),
                eq("body")
        );
    }

    @Test
    void shouldCreateEventNotification() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);
        Event event = mock(Event.class);
        Apartment apartment = mock(Apartment.class);

        when(profile.getApartment()).thenReturn(apartment);
        when(profile.getUser()).thenReturn(user);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(profile.getName()).thenReturn("Марк");

        when(event.getId()).thenReturn(10L);
        when(event.getName()).thenReturn("Birthday");

        Notification savedNotification = mock(Notification.class);

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(savedNotification);

        notificationService.handleEventNotification(user, event, true);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification notification = captor.getValue();

        assertEquals(NotificationType.EVENT_CREATED, notification.getType());

        assertEquals(EntityType.EVENT, notification.getEntityType());

        assertEquals(10L, notification.getPayload().get("eventId"));

        assertEquals("Марк", notification.getPayload().get("userName"));

        assertEquals("Birthday", notification.getPayload().get("eventName"));
    }

    @Test
    void shouldCreateTaskNotificationAndSendPush() {
        User createdBy = mock(User.class);

        Profile creatorProfile = mock(Profile.class);
        Profile assignedTo = mock(Profile.class);

        User assignedUser = mock(User.class);

        Task task = mock(Task.class);

        DeviceToken token1 = mock(DeviceToken.class);
        DeviceToken token2 = mock(DeviceToken.class);

        when(createdBy.getCurrentProfile()).thenReturn(creatorProfile);

        when(createdBy.getMale()).thenReturn(true);

        when(creatorProfile.getId()).thenReturn(1L);
        when(assignedTo.getId()).thenReturn(2L);

        when(creatorProfile.getName()).thenReturn("Марк");

        when(task.getId()).thenReturn(10L);
        when(task.getName()).thenReturn("Wash dishes");
        when(task.getPoints()).thenReturn((short)50);

        when(assignedTo.getUser()).thenReturn(assignedUser);

        when(assignedUser.getDeviceTokens())
                .thenReturn(Set.of(token1, token2));

        when(notificationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        when(iNotificationTextFactory.getTitle(any()))
                .thenReturn("title");

        when(iNotificationTextFactory.getBody(
                any(),
                any(),
                anyBoolean(),
                anyBoolean()
        )).thenReturn("body");

        notificationService.handleTaskCreate(
                createdBy,
                assignedTo,
                task
        );

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification notification = captor.getValue();

        assertEquals(NotificationType.TASK_CREATED,
                notification.getType());

        assertEquals(EntityType.TASK,
                notification.getEntityType());

        assertEquals(10L,
                notification.getPayload().get("taskId"));

        assertEquals("Wash dishes",
                notification.getPayload().get("taskName"));

        assertEquals("Марк",
                notification.getPayload().get("userName"));

        assertEquals((short)50,
                notification.getPayload().get("points"));

        verify(profileNotificationRepository)
                .save(any(ProfileNotification.class));

        verify(iPushNotificationService).send(
                anyList(),
                eq("title"),
                eq("body")
        );
    }

    @Test
    void shouldCreateTaskDoneNotification() {
        User actor = mock(User.class);

        Profile profile = mock(Profile.class);

        Task task = mock(Task.class);

        when(actor.getCurrentProfile()).thenReturn(profile);

        when(actor.getName()).thenReturn("Марк");

        when(task.getCompletedAt())
                .thenReturn(Instant.now());

        when(task.getId()).thenReturn(10L);

        when(task.getName()).thenReturn("Wash dishes");

        when(task.getPoints()).thenReturn((short)50);

        when(notificationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        notificationService.handleTaskSwitchStatus(actor, task);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification notification = captor.getValue();

        assertEquals(NotificationType.TASK_DONE,
                notification.getType());

        assertEquals(EntityType.TASK,
                notification.getEntityType());

        assertEquals(10L,
                notification.getPayload().get("taskId"));

        assertEquals("Wash dishes",
                notification.getPayload().get("taskName"));

        assertEquals("Марк",
                notification.getPayload().get("userName"));

        assertEquals((short)50,
                notification.getPayload().get("points"));
    }

    @Test
    void shouldCreateTaskReopenedNotification() {
        User actor = mock(User.class);

        Profile profile = mock(Profile.class);

        Task task = mock(Task.class);

        when(actor.getCurrentProfile()).thenReturn(profile);

        when(actor.getName()).thenReturn("Марк");

        when(task.getCompletedAt()).thenReturn(null);

        when(task.getId()).thenReturn(10L);

        when(task.getName()).thenReturn("Wash dishes");

        when(task.getPoints()).thenReturn((short)50);

        when(notificationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        notificationService.handleTaskSwitchStatus(actor, task);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification notification = captor.getValue();

        assertEquals(NotificationType.TASK_REOPENED,
                notification.getType());
    }

    @Test
    void shouldCreateNotificationsForEachDeletedTask() {
        User user = mock(User.class);

        Profile profile = mock(Profile.class);

        Task task1 = mock(Task.class);
        Task task2 = mock(Task.class);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(profile.getName()).thenReturn("Марк");

        when(task1.getId()).thenReturn(1L);
        when(task1.getName()).thenReturn("Task 1");
        when(task1.getPoints()).thenReturn((short)10);

        when(task2.getId()).thenReturn(2L);
        when(task2.getName()).thenReturn("Task 2");
        when(task2.getPoints()).thenReturn((short)20);

        when(notificationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        notificationService.handleManyTasksDelete(
                user,
                List.of(task1, task2)
        );

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository, times(2))
                .save(captor.capture());

        List<Notification> notifications =
                captor.getAllValues();

        assertEquals(2, notifications.size());

        assertEquals(NotificationType.TASK_DELETED,
                notifications.get(0).getType());

        assertEquals("Task 1",
                notifications.get(0).getPayload().get("taskName"));

        assertEquals("Task 2",
                notifications.get(1).getPayload().get("taskName"));
    }

    @Test
    void shouldCreateExpenseNotification() {
        User createdBy = mock(User.class);

        Profile profile = mock(Profile.class);

        Expense expense = mock(Expense.class);

        Apartment apartment = mock(Apartment.class);

        when(createdBy.getCurrentProfile())
                .thenReturn(profile);

        when(profile.getName())
                .thenReturn("Марк");

        when(expense.getName())
                .thenReturn("Покупки");

        when(expense.getAmount())
                .thenReturn(1500);

        when(notificationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        when(profile.getApartment()).thenReturn(apartment);
        when(apartment.getId()).thenReturn(1);

        notificationService.handleExpenseCreate(
                createdBy,
                expense
        );

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository)
                .save(captor.capture());

        Notification notification = captor.getValue();

        assertEquals(NotificationType.EXPENSE_CREATED,
                notification.getType());

        assertEquals(EntityType.EXPENSE,
                notification.getEntityType());

        assertEquals("Марк",
                notification.getPayload().get("userName"));

        assertEquals("Покупки",
                notification.getPayload().get("expenseName"));

        assertEquals(
                1500,
                notification.getPayload().get("expenseAmount")
        );
    }

    @Test
    void shouldCreateNotificationsForEachDeletedExpense() {
        User createdBy = mock(User.class);
        Profile profile = mock(Profile.class);

        Expense expense1 = mock(Expense.class);
        Expense expense2 = mock(Expense.class);

        when(createdBy.getCurrentProfile())
                .thenReturn(profile);

        when(profile.getName())
                .thenReturn("Марк");

        when(expense1.getName())
                .thenReturn("Продукты");
        when(expense1.getAmount())
                .thenReturn(1000);

        when(expense2.getName())
                .thenReturn("Аренда");
        when(expense2.getAmount())
                .thenReturn(30000);

        when(notificationRepository.saveAll(anyList()))
                .thenAnswer(i -> i.getArgument(0));

        notificationService.handleManyExpensesDelete(
                createdBy,
                List.of(expense1, expense2)
        );

        ArgumentCaptor<List<Notification>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(notificationRepository)
                .saveAll(captor.capture());

        List<Notification> notifications = captor.getValue();

        assertEquals(2, notifications.size());

        Notification n1 = notifications.get(0);
        Notification n2 = notifications.get(1);

        assertEquals(NotificationType.EXPENSE_DELETED,
                n1.getType());
        assertEquals("Продукты",
                n1.getPayload().get("expenseName"));

        assertEquals(NotificationType.EXPENSE_DELETED,
                n2.getType());
        assertEquals("Аренда",
                n2.getPayload().get("expenseName"));
    }

    @Test
    void shouldCreateRuleCreatedNotification() {
        User createdBy = mock(User.class);
        Profile profile = mock(Profile.class);
        Rule rule = mock(Rule.class);
        Apartment apartment = mock(Apartment.class);

        when(profile.getApartment()).thenReturn(apartment);
        when(apartment.getId()).thenReturn(1);

        when(createdBy.getCurrentProfile()).thenReturn(profile);
        when(profile.getName()).thenReturn("Марк");
        when(rule.getText()).thenReturn("Не шуметь после 23:00");

        when(notificationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        notificationService.handleRuleUpdate(
                createdBy,
                rule,
                true
        );

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification notification = captor.getValue();

        assertEquals(NotificationType.RULE_CREATED,
                notification.getType());

        assertEquals(EntityType.RULE,
                notification.getEntityType());

        assertEquals("Марк",
                notification.getPayload().get("userName"));

        assertEquals("Не шуметь после 23:00",
                notification.getPayload().get("ruleText"));
    }

    @Test
    void shouldCreateRuleDeletedNotification() {
        User createdBy = mock(User.class);
        Profile profile = mock(Profile.class);
        Rule rule = mock(Rule.class);
        Apartment apartment = mock(Apartment.class);

        when(profile.getApartment()).thenReturn(apartment);
        when(apartment.getId()).thenReturn(1);

        when(createdBy.getCurrentProfile()).thenReturn(profile);
        when(profile.getName()).thenReturn("Марк");
        when(rule.getText()).thenReturn("No smoking");

        when(notificationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        notificationService.handleRuleUpdate(
                createdBy,
                rule,
                false
        );

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification notification = captor.getValue();

        assertEquals(NotificationType.RULE_DELETED,
                notification.getType());
    }

    @ParameterizedTest
    @MethodSource("roles")
    void shouldHandleRoleChange(Role role, NotificationType expectedType) {
        Profile profile = mock(Profile.class);

        when(profile.getName()).thenReturn("Марк");

        when(notificationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        notificationService.handleRoleChange(profile, role);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        assertEquals(expectedType,
                captor.getValue().getType());

        verify(profileNotificationRepository)
                .save(any(ProfileNotification.class));
    }

    static Stream<Arguments> roles() {
        return Stream.of(
                Arguments.of(Role.ADMIN, NotificationType.USER_ADMIN),
                Arguments.of(Role.INHABITANT, NotificationType.USER_INHABITANT),
                Arguments.of(Role.CREATOR, NotificationType.USER_CREATOR)
        );
    }

    @Test
    void shouldReturnUnreadCount() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);

        when(user.getCurrentProfile()).thenReturn(profile);
        when(profileNotificationRepository.countByKey_ProfileAndIsReadFalse(profile))
                .thenReturn(5);

        Integer result = notificationService.getUnreadCount(user);

        assertEquals(5, result);

        verify(profileNotificationRepository)
                .countByKey_ProfileAndIsReadFalse(profile);
    }

    @Test
    void shouldReturnPersonalNotifications() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);

        Notification notification = mock(Notification.class);
        ProfileNotification profileNotification = mock(ProfileNotification.class);
        ProfileNotificationKey key = mock(ProfileNotificationKey.class);
        User actorUser = mock(User.class);
        Profile actorProfile = mock(Profile.class);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(profileNotificationRepository.findAllByKey_Profile(
                eq(profile),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(profileNotification)));

        when(profileNotification.getKey()).thenReturn(key);
        when(key.getNotification()).thenReturn(notification);

        when(profileNotification.getRead()).thenReturn(false);

        when(notification.getType()).thenReturn(NotificationType.TASK_CREATED);
        when(notification.getPayload()).thenReturn(Map.of("k", "v"));
        when(notification.getActor()).thenReturn(actorProfile);
        when(actorProfile.getUser()).thenReturn(actorUser);
        when(actorUser.getMale()).thenReturn(true);

        when(iNotificationTextFactory.getBody(
                any(),
                any(),
                anyBoolean(),
                anyBoolean()
        )).thenReturn("body text");

        List<NotificationDto> result =
                notificationService.findPersonal(user, (short) 0, (short) 10);

        assertEquals(1, result.size());

        NotificationDto dto = result.get(0);

        assertEquals("body text", dto.text());
        assertFalse(dto.isRead());

        verify(profileNotificationRepository).findAllByKey_Profile(
                eq(profile),
                any(PageRequest.class)
        );

        verify(iNotificationTextFactory).getBody(
                eq(NotificationType.TASK_CREATED),
                any(),
                eq(true),
                eq(true)
        );
    }

    @Test
    void shouldMarkNotificationAsRead() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);

        ProfileNotification pn = mock(ProfileNotification.class);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(profileNotificationRepository.findByKey_ProfileAndKey_Notification_Id(
                profile,
                10L
        )).thenReturn(Optional.of(pn));

        when(pn.getRead()).thenReturn(false);

        notificationService.markAsRead(10L, user);

        verify(pn).setRead(true);

        verify(profileNotificationRepository).save(pn);
    }

    @Test
    void shouldThrowWhenNotificationNotFound() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(profileNotificationRepository.findByKey_ProfileAndKey_Notification_Id(
                profile,
                10L
        )).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                notificationService.markAsRead(10L, user)
        );

        verify(profileNotificationRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenAlreadyRead() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);

        ProfileNotification pn = mock(ProfileNotification.class);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(profileNotificationRepository.findByKey_ProfileAndKey_Notification_Id(
                profile,
                10L
        )).thenReturn(Optional.of(pn));

        when(pn.getRead()).thenReturn(true);

        assertThrows(BadRequestException.class, () ->
                notificationService.markAsRead(10L, user)
        );

        verify(profileNotificationRepository, never()).save(any());
    }

    @Test
    void shouldMarkAllNotificationsAsRead() {
        User user = mock(User.class);
        Profile profile = mock(Profile.class);

        ProfileNotification n1 = mock(ProfileNotification.class);
        ProfileNotification n2 = mock(ProfileNotification.class);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(profileNotificationRepository.findAllByKey_ProfileAndIsReadFalse(profile))
                .thenReturn(List.of(n1, n2));

        notificationService.markAsReadAll(user);

        verify(profileNotificationRepository)
                .findAllByKey_ProfileAndIsReadFalse(profile);

        verify(n1).setRead(true);
        verify(n2).setRead(true);

        verify(profileNotificationRepository)
                .saveAll(List.of(n1, n2));
    }

}
