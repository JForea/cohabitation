package com.example.backend.repositories;

import com.example.backend.entities.Task;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface TaskRepository extends ListCrudRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    Optional<Task> findByCreatedBy_Apartment_IdAndId(Integer apartmentId, Long id);
}
