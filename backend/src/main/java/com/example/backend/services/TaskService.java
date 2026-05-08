package com.example.backend.services;

import com.example.backend.dtos.in.tasks.CreateTaskDto;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.common.StatusResponse;
import com.example.backend.dtos.out.tasks.TaskDto;
import com.example.backend.entities.*;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.repositories.NotificationRepository;
import com.example.backend.repositories.ProfileNotificationRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.TaskRepository;
import com.example.backend.specifications.TaskSpecifications;
import com.example.backend.types.EntityType;
import com.example.backend.types.NotificationType;
import com.example.backend.types.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    private final ProfileRepository profileRepository;

    private final NotificationRepository notificationRepository;

    private final ProfileNotificationRepository profileNotificationRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProfileRepository profileRepository,
            NotificationRepository notificationRepository,
            ProfileNotificationRepository profileNotificationRepository) {
        this.taskRepository = taskRepository;
        this.profileRepository = profileRepository;
        this.notificationRepository = notificationRepository;
        this.profileNotificationRepository = profileNotificationRepository;
    }

    private Map<String, Object> getBaseNotificationPayload(Profile profile, Task task) {
        return Map.of(
                "taskId", task.getId(),
                "taskName", task.getName(),
                "userName", profile.getName()
        );
    }

    @Transactional
    public IdResponse<Long> create(User user, CreateTaskDto dto) {
        Profile creatorProfile = user.getCurrentProfile();

        Profile assignedProfile = null;
        if (dto.assignedTo() != null)
            assignedProfile = profileRepository.findById(dto.assignedTo()).orElseThrow(
                    () -> new ResourceNotFoundException("Assigned user not found.")
            );

        Task task = taskRepository.save(new Task(
                creatorProfile,
                assignedProfile,
                dto.name(),
                dto.description(),
                dto.room(),
                dto.priority(),
                dto.points(),
                dto.repeatTime(),
                dto.dueDate()
        ));

        Notification notification = new Notification(
                creatorProfile,
                NotificationType.TASK_CREATED,
                EntityType.TASK,
                getBaseNotificationPayload(creatorProfile, task)
        );

        notification = notificationRepository.save(notification);

        if (assignedProfile != null && !Objects.equals(assignedProfile.getId(), creatorProfile.getId())) {
            ProfileNotification profileNotification = new ProfileNotification(
                assignedProfile,
                notification
            );

            profileNotificationRepository.save(profileNotification);
        }

        return new IdResponse<>(task.getId());
    }

    public List<TaskDto> getTasks(
            Integer apartmentId,
            Short cntPerPage,
            Short page,
            Integer assignedTo,
            Boolean done
    ) {
        Specification<Task> spec = Specification
                .where(TaskSpecifications.byApartment(apartmentId))
                .and(TaskSpecifications.assignedToUser(assignedTo))
                .and(TaskSpecifications.taskDoneStatusIs(done));

        return taskRepository.findAll(spec, PageRequest.of(page, cntPerPage))
                .map(
                TaskDto::new
        ).toList();
    }

    @Transactional
    public StatusResponse switchTaskStatus(User user, Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new ResourceNotFoundException("Task not found.")
        );

        Profile userProfile = user.getCurrentProfile();
        if (
                task.getCompletedAt() != null &&
                !Objects.equals(task.getCompletedBy().getId(), userProfile.getId()) &&
                userProfile.getRole() == Role.INHABITANT
        )
            throw new AccessForbiddenException("You can't change status of this task, because you are not the " +
                    "one who completed it.");

        Map<String, Object> payload = new HashMap<>(getBaseNotificationPayload(user.getCurrentProfile(), task));
        payload.put("points", task.getPoints());

        Profile profile;
        if (task.getCompletedAt() != null) {
            profile = task.getCompletedBy();
            profile.addPoints(Integer.valueOf(-task.getPoints()).shortValue());

            task.setCompletedAt(null);
            task.setCompletedBy(null);
        } else {
            profile = user.getCurrentProfile();
            profile.addPoints(task.getPoints());

            task.setCompletedBy(profile);
            task.setCompletedAt(Instant.now());
        }

        profileRepository.save(profile);
        taskRepository.save(task);

        notificationRepository.save(
                new Notification(
                user.getCurrentProfile(),
                task.getCompletedAt() != null ? NotificationType.TASK_DONE : NotificationType.TASK_REOPENED,
                EntityType.TASK,
                payload
                )
        );

        return new StatusResponse(task.getCompletedAt() != null);
    }

    public void deleteOne(Integer apartmentId, User user, Long taskId) {
        Task task = taskRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, taskId).orElseThrow(
                () -> new ResourceNotFoundException("Task not found.")
        );

        Profile profile = user.getCurrentProfile();

        if (!Objects.equals(task.getCreatedBy().getId(), profile.getId()) && profile.getRole() == Role.INHABITANT) {
            throw new AccessForbiddenException("You can't delete this task.");
        }

        taskRepository.delete(task);
    }
}
