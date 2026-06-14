package com.example.backend.services;

import com.example.backend.dtos.in.tasks.CreateTaskDto;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.common.StatusResponse;
import com.example.backend.dtos.out.tasks.CreateTaskResponse;
import com.example.backend.dtos.out.tasks.TaskDto;
import com.example.backend.entities.*;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.BadRequestException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.exceptions.StateConflictException;
import com.example.backend.intefaces.ITaskLoadService;
import com.example.backend.intefaces.TaskNotificationHandler;
import com.example.backend.repositories.*;
import com.example.backend.specifications.TaskSpecifications;
import com.example.backend.types.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    private final ProfileRepository profileRepository;

    private final TaskNotificationHandler taskNotificationHandler;

    private final TaskRepeatRuleRepository taskRepeatRuleRepository;

    private final ITaskLoadService iTaskLoadService;

    public TaskService(
            TaskRepository taskRepository,
            ProfileRepository profileRepository,
            TaskNotificationHandler taskNotificationHandler,
            TaskRepeatRuleRepository taskRepeatRuleRepository,
            ITaskLoadService iTaskLoadService) {
        this.taskRepository = taskRepository;
        this.profileRepository = profileRepository;
        this.taskNotificationHandler = taskNotificationHandler;
        this.taskRepeatRuleRepository = taskRepeatRuleRepository;
        this.iTaskLoadService = iTaskLoadService;
    }

    @Transactional
    public CreateTaskResponse create(User user, CreateTaskDto dto) {
        Profile creatorProfile = user.getCurrentProfile();

        Profile assignedProfile = null;

        if (dto.assignedTo() != null) {
            assignedProfile = profileRepository.findById(dto.assignedTo()).orElseThrow(
                    () -> new ResourceNotFoundException("Assigned user not found.")
            );

            if (assignedProfile.getLeftAt() != null)
                throw new StateConflictException(
                        "Can't assign task for user, who left the apartment."
                );
        } else if (Boolean.TRUE.equals(dto.autoAssign())) {
            List<Profile> candidates = profileRepository.findAllByApartment_IdAndLeftAtNull(
                    creatorProfile.getApartment().getId()
            );

            if (candidates.isEmpty())
                throw new StateConflictException(
                        "No available users for automatic assignment."
                );

            Map<Long, Double> loads = iTaskLoadService.calculateProfileLoad(
                    creatorProfile.getApartment().getId(),
                    candidates,
                    dto.dueDate().minusDays(30),
                    dto.dueDate().plusDays(30)
            );

            assignedProfile = candidates.stream()
                    .min(Comparator.comparingDouble(
                            profile -> loads.getOrDefault(profile.getId(), 0.0)
                    ))
                    .orElseThrow();
        }

        TaskRepeatRule repeatRule = null;

        if (dto.repeatRule() != null) {
            List<Profile> assignedProfiles =
                    profileRepository.findAllByIdInAndLeftAtNull(
                            dto.repeatRule().assignedIds()
                    );

            if (assignedProfiles.size() != dto.repeatRule().assignedIds().size())
                throw new BadRequestException(
                        "Request contains invalid assigned ids."
                );

            repeatRule = new TaskRepeatRule(
                    creatorProfile,
                    assignedProfiles,
                    dto.name(),
                    dto.description(),
                    dto.room(),
                    dto.priority(),
                    dto.points(),
                    dto.repeatRule().intervalDays(),
                    dto.dueDate(),
                    dto.repeatRule().endDate()
            );

            taskRepeatRuleRepository.save(repeatRule);
        }

        Task task = taskRepository.save(new Task(
                creatorProfile,
                assignedProfile,
                dto.name(),
                dto.description(),
                dto.room(),
                dto.priority(),
                dto.points(),
                dto.dueDate(),
                repeatRule
        ));

        taskNotificationHandler.handleTaskCreate(
                user,
                assignedProfile,
                task
        );

        return new CreateTaskResponse(task);
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

        taskNotificationHandler.handleTaskSwitchStatus(user, task);

        return new StatusResponse(task.getCompletedAt() != null);
    }

    @Transactional
    public void deleteOne(Integer apartmentId, User user, Long taskId) {
        Task task = taskRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, taskId).orElseThrow(
                () -> new ResourceNotFoundException("Task not found.")
        );

        Profile profile = user.getCurrentProfile();

        if ((!Objects.equals(task.getCreatedBy().getId(), profile.getId()) || task.getCompletedAt() != null) &&
                profile.getRole() == Role.INHABITANT) {
            throw new AccessForbiddenException("You can't delete this task.");
        }

        taskNotificationHandler.handleManyTasksDelete(user, List.of(task));

        if (task.getCompletedAt() != null)
            task.setDeletedAt(Instant.now());
        else
            taskRepository.delete(task);
    }

    @Transactional
    public void deleteMany(User user, Integer apartmentId, List<Long> taskIds) {
        List<Task> tasks = taskRepository.findAllByCreatedBy_Apartment_IdAndIdIn(
                apartmentId,
                taskIds
        );

        if (tasks.size() != new HashSet<>(taskIds).size()) {
            throw new BadRequestException("Invalid task id.");
        }

        Profile profile = user.getCurrentProfile();

        for (Task task : tasks) {
            boolean isOwner = Objects.equals(task.getCreatedBy().getId(), profile.getId());
            boolean isAdmin = profile.getRole() != Role.INHABITANT;

            if (!isOwner && !isAdmin) {
                throw new AccessForbiddenException("You can't delete some tasks.");
            }
        }

        List<Task> completedTasks = tasks.stream()
                .filter(task -> task.getCompletedAt() != null)
                .toList();

        List<Task> notCompletedTasks = tasks.stream()
                .filter(task -> task.getCompletedAt() == null)
                .toList();

        Instant now = Instant.now();

        for (Task task : completedTasks) {
            task.setDeletedAt(now);
        }

        taskNotificationHandler.handleManyTasksDelete(user, tasks);

        taskRepository.deleteAll(notCompletedTasks);
    }
}
