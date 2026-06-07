package com.example.backend.services;

import com.example.backend.entities.Profile;
import com.example.backend.entities.TaskRepeatRule;
import com.example.backend.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskLoadService {

    private static final double COMPLETION_COEFFICIENT = 0.3;
    private static final double OVERDUE_COEFFICIENT = 0.8;

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
        Map<Long, Double> loads = new HashMap<>();

        Integer apartmentId = rule.getCreatedBy().getApartment().getId();

        for (Profile profile : candidates) {
            LocalDate joinedAt = profile.getJoinedAt()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate actualStart = joinedAt.isAfter(calculationStart)
                    ? joinedAt
                    : calculationStart;

            double load = calculateProfileLoad(
                    apartmentId,
                    profile.getId(),
                    actualStart,
                    calculationEnd
            );

            loads.put(profile.getId(), load);
        }

        return loads;
    }

    private double calculateProfileLoad(
            Integer apartmentId,
            Long profileId,
            LocalDate calculationStart,
            LocalDate calculationEnd
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

        return assignedPoints
                + COMPLETION_COEFFICIENT * completedPoints
                - OVERDUE_COEFFICIENT * overduePoints;
    }

    private Map<Long, Integer> toMap(List<Object[]> rows) {
        Map<Long, Integer> result = new HashMap<>();

        for (Object[] row : rows) {
            Long profileId = (Long) row[0];
            Number points = (Number) row[1];

            result.put(profileId, points.intValue());
        }

        return result;
    }
}