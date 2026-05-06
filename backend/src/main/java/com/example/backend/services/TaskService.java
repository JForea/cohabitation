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
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.TaskRepository;
import com.example.backend.specifications.TaskSpecifications;
import com.example.backend.types.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProfileRepository profileRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProfileRepository profileRepository
    ) {
        this.taskRepository = taskRepository;
        this.profileRepository = profileRepository;
    }

    public IdResponse<Long> create(User user, Integer apartmentId, CreateTaskDto dto) {
        Profile creatorProfile = profileRepository.findByUserAndApartment_id(user, apartmentId).orElseThrow(
                () -> new AccessForbiddenException("You can't create tasks in this apartment.")
        );

        if (creatorProfile.getLeftAt() != null)
            throw new AccessForbiddenException("You can't create tasks in this apartment.");

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
    public StatusResponse switchTaskStatus(Integer apartmentId, User user, Long taskId) {
        Task task = taskRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, taskId).orElseThrow(
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
