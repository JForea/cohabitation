package com.example.backend.services;

import com.example.backend.dtos.in.tasks.CreateTaskDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.Task;
import com.example.backend.entities.User;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.repositories.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProfileRepository profileRepository;
    private final ApartmentRepository apartmentRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProfileRepository profileRepository,
            ApartmentRepository apartmentRepository
    ) {
        this.taskRepository = taskRepository;
        this.profileRepository = profileRepository;
        this.apartmentRepository = apartmentRepository;
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
}
