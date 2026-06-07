package com.example.backend.repositories;

import com.example.backend.entities.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.time.Instant;
import java.time.LocalDate;
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
    boolean existsByRepeatRule_IdAndDueTime(Long repeatRuleId, LocalDate dueTime);
    boolean existsByRepeatRule_IdAndCompletedAtNullAndDueTimeGreaterThanEqual(Long id, LocalDate now);
    @Query("""
    SELECT COALESCE(SUM(t.points), 0)
    FROM Task t
    WHERE t.assignedTo.id = :profileId
        AND t.createdBy.apartment.id = :apartmentId
        AND t.completedAt IS NULL
        AND t.dueTime >= :calculationStart
        AND t.dueTime <= :calculationEnd
    """)
    int calculateAssignedPointsForProfile(
            Integer apartmentId,
            Long profileId,
            LocalDate calculationStart,
            LocalDate calculationEnd
    );
    @Query("""
    SELECT COALESCE(SUM(t.points), 0)
    FROM Task t
    WHERE t.completedBy.id = :profileId
        AND t.createdBy.apartment.id = :apartmentId
        AND t.completedAt IS NOT NULL
        AND t.completedAt >= :calculationStart
        AND t.completedAt < :calculationEnd
    """)
    int calculateCompletedPointsForProfile(
            Integer apartmentId,
            Long profileId,
            Instant calculationStart,
            Instant calculationEnd
    );
    @Query("""
    SELECT COALESCE(SUM(t.points), 0)
    FROM Task t
    WHERE t.assignedTo.id = :profileId
        AND t.createdBy.apartment.id = :apartmentId
        AND t.deletedAt IS NULL
        AND t.completedAt IS NULL
        AND t.dueTime < :today
        AND t.dueTime >= :calculationStart
        AND t.dueTime <= :calculationEnd
    """)
    int calculateOverduePointsForProfile(
            Integer apartmentId,
            Long profileId,
            LocalDate calculationStart,
            LocalDate calculationEnd,
            LocalDate today
    );
    Optional<Task> findTopByRepeatRule_IdOrderByDueTimeDesc(
            Long repeatRuleId
    );
}
