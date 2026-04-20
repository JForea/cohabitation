package com.example.backend.repositories;

import com.example.backend.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;


public interface TaskRepository extends ListCrudRepository<Task, Long> {
    Page<Task> findByApartment_Id(Integer id, Pageable pageable);
}
