package com.example.backend.services;

import com.example.backend.entities.Profile;
import com.example.backend.entities.TaskRepeatRule;
import com.example.backend.intefaces.ITaskLoadService;
import com.example.backend.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class TaskLoadService implements ITaskLoadService {

    private static final double COMPLETION_COEFFICIENT = 0.3;
    private static final double OVERDUE_COEFFICIENT = 0.8;
    private static final double LATE_COMPLETION_COEFFICIENT = 0.05;

    private final TaskRepository taskRepository;

    public TaskLoadService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Map<Long, Double> calculateProfileLoad(
            TaskRepeatRule rule,
            Collection<Profile> candidates,
            LocalDate calculationStart,
            LocalDate calculationEnd
    ) {
        Integer apartmentId = rule.getCreatedBy().getApartment().getId();

        return calculateProfileLoad(
                apartmentId,
                candidates,
                calculationStart,
                calculationEnd
        );
    }

    public Map<Long, Double> calculateProfileLoad(
            Integer apartmentId,
            Collection<Profile> candidates,
            LocalDate calculationStart,
            LocalDate calculationEnd
    ) {
        Map<Long, Double> loads = new HashMap<>();

        long loadWindow = Math.max(
                1,
                ChronoUnit.DAYS.between(calculationStart, calculationEnd) + 1
        );

        for (Profile profile : candidates) {
            LocalDate joinedAt = profile.getJoinedAt()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate actualStart = joinedAt.isAfter(calculationStart)
                    ? joinedAt
                    : calculationStart;

            long daysInApartment = Math.max(
                    1,
                    ChronoUnit.DAYS.between(actualStart, calculationEnd) + 1
            );

            double load = calculateProfileLoad(
                    apartmentId,
                    profile.getId(),
                    actualStart,
                    calculationEnd,
                    daysInApartment,
                    loadWindow
            );

            loads.put(profile.getId(), load);
        }

        return loads;
    }

    private double calculateProfileLoad(
            Integer apartmentId,
            Long profileId,
            LocalDate calculationStart,
            LocalDate calculationEnd,
            long daysInApartment,
            long loadWindow
    ) {
        Instant calculationStartInstant = calculationStart
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant calculationEndInstant = calculationEnd
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        int assignedPoints = taskRepository.calculateAssignedPointsForProfile(
                apartmentId,
                profileId,
                calculationStart,
                calculationEnd
        );

        int completedPoints = taskRepository.calculateCompletedPointsForProfile(
                apartmentId,
                profileId,
                calculationStartInstant,
                calculationEndInstant
        );

        int overduePoints = taskRepository.calculateOverduePointsForProfile(
                apartmentId,
                profileId,
                calculationStart,
                calculationEnd,
                LocalDate.now()
        );

        int lateCompletedPenalty = taskRepository.calculateLateCompletedPenaltyForProfile(
                apartmentId,
                profileId,
                calculationStartInstant,
                calculationEndInstant
        );

        long normalizationDays = Math.min(
                Math.max(daysInApartment, 1),
                loadWindow
        );

        return (
                assignedPoints
                        + COMPLETION_COEFFICIENT * completedPoints
                        + OVERDUE_COEFFICIENT * overduePoints
                        - LATE_COMPLETION_COEFFICIENT * lateCompletedPenalty
        ) / normalizationDays;
    }
}