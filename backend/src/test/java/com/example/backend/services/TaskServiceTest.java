package com.example.backend.services;

import com.example.backend.dtos.in.tasks.CreateTaskDto;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.common.StatusResponse;
import com.example.backend.dtos.out.tasks.TaskDto;
import com.example.backend.entities.Profile;
import com.example.backend.entities.Task;
import com.example.backend.entities.User;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.exceptions.StateConflictException;
import com.example.backend.intefaces.TaskNotificationHandler;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.TaskRepository;
import com.example.backend.types.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private TaskNotificationHandler taskNotificationHandler;

    @InjectMocks
    private TaskService taskService;

    @Test
     void shouldCreateTaskWithoutAssignedUser() {
        User user = mock(User.class);

        Profile creator = mock(Profile.class);

        CreateTaskDto dto = mock(CreateTaskDto.class);

        Task savedTask = mock(Task.class);

        when(user.getCurrentProfile()).thenReturn(creator);

        when(dto.assignedTo()).thenReturn(null);

        when(taskRepository.save(any())).thenReturn(savedTask);

        when(savedTask.getId()).thenReturn(10L);

        IdResponse<Long> result = taskService.create(user, dto);

        assertEquals(10L, result.id());

        verify(taskRepository).save(any(Task.class));

        verify(taskNotificationHandler).handleTaskCreate(user, null, savedTask);
    }

    @Test
    void shouldCreateTaskWithAssignedUser() {
        User user = mock(User.class);

        Profile creator = mock(Profile.class);
        Profile assigned = mock(Profile.class);

        CreateTaskDto dto = mock(CreateTaskDto.class);

        Task savedTask = mock(Task.class);

        when(user.getCurrentProfile()).thenReturn(creator);

        when(dto.assignedTo()).thenReturn(2L);

        when(profileRepository.findById(2L)).thenReturn(Optional.of(assigned));

        when(assigned.getLeftAt()).thenReturn(null);

        when(taskRepository.save(any())).thenReturn(savedTask);

        taskService.create(user, dto);

        verify(profileRepository).findById(2L);

        verify(taskNotificationHandler).handleTaskCreate(user, assigned, savedTask);
    }

    @Test
    void shouldThrowWhenAssignedUserNotFound() {
        User user = mock(User.class);

        Profile creator = mock(Profile.class);

        CreateTaskDto dto = mock(CreateTaskDto.class);

        when(user.getCurrentProfile()).thenReturn(creator);

        when(dto.assignedTo()).thenReturn(2L);

        when(profileRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.create(user, dto)
        );

        verify(taskRepository, never()).save(any());

        verifyNoInteractions(taskNotificationHandler);
    }

    @Test
    void shouldThrowWhenAssignedUserLeftApartment() {
        User user = mock(User.class);

        Profile creator = mock(Profile.class);
        Profile assigned = mock(Profile.class);

        CreateTaskDto dto = mock(CreateTaskDto.class);

        when(user.getCurrentProfile()).thenReturn(creator);

        when(dto.assignedTo()).thenReturn(2L);

        when(profileRepository.findById(2L)).thenReturn(Optional.of(assigned));

        when(assigned.getLeftAt()).thenReturn(Instant.now());

        assertThrows(StateConflictException.class, () ->
                taskService.create(user, dto)
        );

        verify(taskRepository, never()).save(any());

        verifyNoInteractions(taskNotificationHandler);
    }

    @Test
    void shouldGetTasks() {
        Task task1 = mock(Task.class);
        Task task2 = mock(Task.class);

        Profile createdBy1 = mock(Profile.class);
        Profile createdBy2 = mock(Profile.class);

        Profile assignedTo1 = mock(Profile.class);
        Profile assignedTo2 = mock(Profile.class);

        when(task1.getCreatedBy()).thenReturn(createdBy1);
        when(task2.getCreatedBy()).thenReturn(createdBy2);

        when(task1.getAssignedTo()).thenReturn(assignedTo1);
        when(task2.getAssignedTo()).thenReturn(assignedTo2);

        when(createdBy1.getId()).thenReturn(1L);
        when(createdBy2.getId()).thenReturn(2L);

        when(assignedTo1.getId()).thenReturn(2L);
        when(assignedTo2.getId()).thenReturn(1L);

        Page<Task> page = new PageImpl<>(
                List.of(task1, task2)
        );

        when(taskRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        List<TaskDto> result = taskService.getTasks(
                1,
                (short) 10,
                (short) 0,
                2,
                false
        );

        assertEquals(2, result.size());

        verify(taskRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void shouldCompleteTask() {
        User user = mock(User.class);

        Profile profile = mock(Profile.class);

        Task task = new Task();

        task.setPoints((short) 10);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        StatusResponse response =
                taskService.switchTaskStatus(user, 1L);

        verify(profile).addPoints((short) 10);

        verify(profileRepository).save(profile);

        verify(taskRepository).save(task);

        verify(taskNotificationHandler)
                .handleTaskSwitchStatus(user, task);

        assertNotNull(task.getCompletedAt());

        assertEquals(profile, task.getCompletedBy());

        assertTrue(response.status());
    }

    @Test
    void shouldReopenOwnTask() {
        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);

        Task task = new Task();
        task.setPoints((short) 10);
        task.setCompletedBy(profile);
        task.setCompletedAt(Instant.now());

        when(user.getCurrentProfile()).thenReturn(profile);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        StatusResponse response =
                taskService.switchTaskStatus(user, 1L);

        assertNull(task.getCompletedAt());

        assertNull(task.getCompletedBy());

        verify(profile).addPoints((short) -10);

        verify(profileRepository).save(profile);

        verify(taskRepository).save(task);

        assertFalse(response.status());
    }

    @Test
    void shouldAllowAdminToReopenForeignTask() {
        User user = mock(User.class);

        Profile admin = mock(Profile.class);
        when(admin.getId()).thenReturn(1L);
        when(admin.getRole()).thenReturn(Role.ADMIN);

        Profile completedBy = mock(Profile.class);
        when(completedBy.getId()).thenReturn(2L);

        Task task = new Task();
        task.setPoints((short) 10);
        task.setCompletedAt(Instant.now());
        task.setCompletedBy(completedBy);

        when(user.getCurrentProfile()).thenReturn(admin);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        StatusResponse response =
                taskService.switchTaskStatus(user, 1L);

        verify(completedBy).addPoints((short) -10);

        assertNull(task.getCompletedAt());

        assertNull(task.getCompletedBy());

        assertFalse(response.status());
    }

    @Test
    void shouldThrowOnSwitchStatusWhenTaskNotFound() {
        User user = mock(User.class);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.switchTaskStatus(user, 1L)
        );
    }

    @Test
    void shouldThrowWhenInhabitantReopensForeignTask() {
        User user = mock(User.class);

        Profile actor = mock(Profile.class);
        Profile completedBy = mock(Profile.class);

        Task task = mock(Task.class);

        when(user.getCurrentProfile()).thenReturn(actor);

        when(actor.getId()).thenReturn(1L);
        when(actor.getRole()).thenReturn(Role.INHABITANT);

        when(completedBy.getId()).thenReturn(2L);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(task.getCompletedAt())
                .thenReturn(Instant.now());

        when(task.getCompletedBy())
                .thenReturn(completedBy);

        assertThrows(AccessForbiddenException.class, () ->
                taskService.switchTaskStatus(user, 1L)
        );

        verify(profileRepository, never()).save(any());

        verify(taskNotificationHandler, never())
                .handleTaskSwitchStatus(any(), any());
    }

    @Test
    void shouldDeleteOwnTask() {
        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);

        Task task = mock(Task.class);
        when(task.getCreatedBy()).thenReturn(profile);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(taskRepository.findByCreatedBy_Apartment_IdAndId(1, 1L))
                .thenReturn(Optional.of(task));

        taskService.deleteOne(1, user, 1L);

        verify(taskNotificationHandler).handleManyTasksDelete(user, List.of(task));

        verify(taskRepository).delete(task);
    }

    @Test
    void shouldThrowWhenInhabitantDeletesForeignTask() {
        User user = mock(User.class);

        Profile actor = mock(Profile.class);
        when(actor.getId()).thenReturn(1L);
        when(actor.getRole()).thenReturn(Role.INHABITANT);

        Profile creator = mock(Profile.class);
        when(actor.getId()).thenReturn(2L);

        Task task = mock(Task.class);
        when(task.getCreatedBy()).thenReturn(creator);

        when(user.getCurrentProfile()).thenReturn(actor);

        when(taskRepository.findByCreatedBy_Apartment_IdAndId(1, 1L))
                .thenReturn(Optional.of(task));

        assertThrows(AccessForbiddenException.class, () ->
                taskService.deleteOne(1, user, 1L)
        );

        verify(taskRepository, never()).delete(Mockito.<Task>any());

        verifyNoInteractions(taskNotificationHandler);
    }

    @Test
    void shouldThrowWhenInhabitantDeletesCompletedTask() {
        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getRole()).thenReturn(Role.INHABITANT);

        Task task = mock(Task.class);
        when(task.getCreatedBy()).thenReturn(profile);
        when(task.getCompletedAt()).thenReturn(Instant.now());

        when(user.getCurrentProfile()).thenReturn(profile);

        when(taskRepository.findByCreatedBy_Apartment_IdAndId(1, 1L))
                .thenReturn(Optional.of(task));

        assertThrows(AccessForbiddenException.class, () ->
                taskService.deleteOne(1, user, 1L)
        );

        verify(taskRepository, never()).delete(Mockito.<Task>any());

        verifyNoInteractions(taskNotificationHandler);
    }

    @Test
    void shouldAllowAdminToDeleteTask() {
        User user = mock(User.class);

        Profile admin = mock(Profile.class);
        when(admin.getId()).thenReturn(1L);
        admin.setRole(Role.ADMIN);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(2L);

        Task task = mock(Task.class);
        when(task.getCreatedBy()).thenReturn(creator);

        when(user.getCurrentProfile()).thenReturn(admin);

        when(taskRepository.findByCreatedBy_Apartment_IdAndId(1, 1L))
                .thenReturn(Optional.of(task));

        taskService.deleteOne(1, user, 1L);

        verify(taskRepository).delete(task);

        verify(taskNotificationHandler).handleManyTasksDelete(user, List.of(task));
    }

    @Test
    void shouldThrowOnDeleteOneWhenTaskNotFound() {
        User user = mock(User.class);

        when(taskRepository.findByCreatedBy_Apartment_IdAndId(1, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.deleteOne(1, user, 1L)
        );

        verify(taskRepository, never()).delete(Mockito.<Task>any());

        verifyNoInteractions(taskNotificationHandler);
    }

    @Test
    void shouldDeleteManyOwnTasks() {
        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);

        Task task1 = mock(Task.class);
        when(task1.getCreatedBy()).thenReturn(profile);

        Task task2 = mock(Task.class);
        when(task2.getCreatedBy()).thenReturn(profile);

        List<Task> tasks = List.of(task1, task2);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(taskRepository.findAllByCreatedBy_Apartment_IdAndIdIn(
                1,
                List.of(1L, 2L)
        )).thenReturn(tasks);

        taskService.deleteMany(user, 1, List.of(1L, 2L));

        verify(taskNotificationHandler)
                .handleManyTasksDelete(user, tasks);

        verify(taskRepository)
                .deleteAll(tasks);
    }

    @Test
    void shouldThrowWhenInhabitantDeletesForeignTasks() {
        User user = mock(User.class);

        Profile actor = mock(Profile.class);
        when(actor.getId()).thenReturn(1L);
        when(actor.getRole()).thenReturn(Role.INHABITANT);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(2L);

        Task task = mock(Task.class);
        when(task.getCreatedBy()).thenReturn(creator);

        when(user.getCurrentProfile()).thenReturn(actor);

        when(taskRepository.findAllByCreatedBy_Apartment_IdAndIdIn(
                1,
                List.of(1L)
        )).thenReturn(List.of(task));

        assertThrows(AccessForbiddenException.class, () ->
                taskService.deleteMany(user, 1, List.of(1L))
        );

        verify(taskRepository, never())
                .deleteAll(Mockito.<List<Task>>any());

        verifyNoInteractions(taskNotificationHandler);
    }

    @Test
    void shouldThrowWhenInhabitantDeletesCompletedTasks() {
        User user = mock(User.class);

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getRole()).thenReturn(Role.INHABITANT);

        Task task = mock(Task.class);
        when(task.getCreatedBy()).thenReturn(profile);
        when(task.getCompletedAt()).thenReturn(Instant.now());

        when(user.getCurrentProfile()).thenReturn(profile);

        when(taskRepository.findAllByCreatedBy_Apartment_IdAndIdIn(
                1,
                List.of(1L)
        )).thenReturn(List.of(task));

        assertThrows(AccessForbiddenException.class, () ->
                taskService.deleteMany(user, 1, List.of(1L))
        );

        verify(taskRepository, never())
                .deleteAll(Mockito.<List<Task>>any());

        verifyNoInteractions(taskNotificationHandler);
    }

    @Test
    void shouldAllowAdminToDeleteManyTasks() {
        User user = mock(User.class);

        Profile admin = mock(Profile.class);
        when(admin.getId()).thenReturn(1L);
        when(admin.getRole()).thenReturn(Role.ADMIN);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(2L);

        Task task = mock(Task.class);
        when(task.getCreatedBy()).thenReturn(creator);

        List<Task> tasks = List.of(task);

        when(user.getCurrentProfile()).thenReturn(admin);

        when(taskRepository.findAllByCreatedBy_Apartment_IdAndIdIn(
                1,
                List.of(1L)
        )).thenReturn(tasks);

        taskService.deleteMany(user, 1, List.of(1L));

        verify(taskRepository)
                .deleteAll(tasks);

        verify(taskNotificationHandler)
                .handleManyTasksDelete(user, tasks);
    }

    @Test
    void shouldDeleteNothingWhenTasksListIsEmpty() {
        User user = mock(User.class);

        Profile profile = mock(Profile.class);

        when(user.getCurrentProfile()).thenReturn(profile);

        when(taskRepository.findAllByCreatedBy_Apartment_IdAndIdIn(
                1,
                List.of(1L)
        )).thenReturn(List.of());

        taskService.deleteMany(user, 1, List.of(1L));

        verify(taskNotificationHandler)
                .handleManyTasksDelete(user, List.of());

        verify(taskRepository)
                .deleteAll(List.of());
    }

}
