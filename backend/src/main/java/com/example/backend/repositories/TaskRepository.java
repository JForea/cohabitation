package com.example.backend.repositories;

import com.example.backend.entities.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends ListCrudRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    Optional<Task> findByCreatedBy_Apartment_IdAndId(Integer apartmentId, Long id);
    List<Task> findAllByCreatedBy_Apartment_IdAndIdIn(Integer apartmentId, List<Long> taskIds);
    @EntityGraph(attributePaths = {
            "assignedTo",
            "assignedTo.user"
    })
    @Query("""
    SELECT t
    FROM Task t
    JOIN t.assignedTo p
    WHERE t.completedAt IS NULL
        AND t.assignedTo IS NOT NULL
        AND t.lastReminderDate IS NULL
        AND p.leftAt IS NULL
    """)
    List<Task> findAllForReminder();
}
