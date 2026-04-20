package com.example.backend.services;

import com.example.backend.dtos.in.tasks.CreateTaskDto;
import com.example.backend.dtos.out.tasks.TaskDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.Task;
import com.example.backend.entities.User;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.TaskRepository;
import com.example.backend.specifications.TaskSpecifications;
import com.example.backend.types.Role;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProfileRepository profileRepository;
    private final ApartmentRepository apartmentRepository;

    private final UserService userService;

    public TaskService(
            TaskRepository taskRepository,
            ProfileRepository profileRepository,
            ApartmentRepository apartmentRepository,
            UserService userService) {
        this.taskRepository = taskRepository;
        this.profileRepository = profileRepository;
        this.apartmentRepository = apartmentRepository;
        this.userService = userService;
    }

    public Long create(User user, Integer apartmentId, CreateTaskDto dto) {
        Profile creatorProfile = profileRepository.findByUserAndApartment_id(user, apartmentId).orElseThrow(
                () -> new AccessForbiddenException("You can't create tasks in this apartment.")
        );

        if (creatorProfile.getLeftAt() != null)
            throw new AccessForbiddenException("You can't create tasks in this apartment.");

        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(
                () -> new ResourceNotFoundException("Apartment not found.")
        );
        Profile assignedProfile = null;
        if (dto.assignedTo() != null)
            assignedProfile = profileRepository.findById(dto.assignedTo()).orElseThrow(
                    () -> new ResourceNotFoundException("Assigned user not found.")
            );

        Task task = taskRepository.save(new Task(
                apartment,
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

        return task.getId();
    }

    public List<TaskDto> getTasks(
            Integer apartmentId,
            User user,
            Short cntPerPage,
            Short page,
            Integer assignedTo,
            Boolean done
    ) {
        Role role = userService.getCurrentUserRoleInApartment(user, apartmentId);

        if (role == null)
            throw new AccessForbiddenException("You can't view tasks in this apartment.");

        Specification<Task> spec = Specification
                .where(TaskSpecifications.byApartment(apartmentId))
                .and(TaskSpecifications.assignedToUser(assignedTo))
                .and(TaskSpecifications.taskDoneStatusIs(done));

        return taskRepository.findAll(spec, PageRequest.of(page, cntPerPage))
                .map(
                TaskDto::new
        ).toList();
    }
}
