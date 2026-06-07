package com.example.backend.services;

import com.example.backend.entities.Profile;
import com.example.backend.entities.Task;
import com.example.backend.entities.TaskRepeatRule;
import com.example.backend.repositories.TaskRepeatRuleRepository;
import com.example.backend.repositories.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaskRepeatGenerationService {

    private static final int LOAD_WINDOW_DAYS = 30;

    private final TaskRepeatRuleRepository taskRepeatRuleRepository;
    private final TaskRepository taskRepository;
    private final TaskLoadService taskLoadService;

    public TaskRepeatGenerationService(
            TaskRepeatRuleRepository taskRepeatRuleRepository,
            TaskRepository taskRepository,
            TaskLoadService taskLoadService
    ) {
        this.taskRepeatRuleRepository = taskRepeatRuleRepository;
        this.taskRepository = taskRepository;
        this.taskLoadService = taskLoadService;
    }

    @Transactional
    public void ensureNextTasksExist() {
        List<TaskRepeatRule> rules = taskRepeatRuleRepository.findAllByActiveTrue();

        for (TaskRepeatRule rule : rules) {
            ensureNextTaskExists(rule);
        }
    }

    private void ensureNextTaskExists(TaskRepeatRule rule) {
        if (rule == null || !Boolean.TRUE.equals(rule.getActive())) {
            System.out.println("Rule inactive");
            return;
        }

        Optional<Task> lastTaskOptional =
                taskRepository.findTopByRepeatRule_IdOrderByDueTimeDesc(rule.getId());

        if (lastTaskOptional.isEmpty()) {
            System.out.println("No previous task. nextDate = " + rule.getStartDate());
            createTaskForDate(rule, rule.getStartDate());
            return;
        }

        Task lastTask = lastTaskOptional.get();

        System.out.println("Last task id = " + lastTask.getId());
        System.out.println("Last task dueTime = " + lastTask.getDueTime());
        System.out.println("Last task completedAt = " + lastTask.getCompletedAt());

        LocalDate nextDate = lastTask.getDueTime().plusDays(rule.getIntervalDays());

        System.out.println("Next date = " + nextDate);
        System.out.println("Today + 1 = " + LocalDate.now().plusDays(1));

        boolean previousCompleted = lastTask.getCompletedAt() != null;
        boolean nextDateIsClose = !nextDate.isAfter(LocalDate.now().plusDays(1));

        System.out.println("previousCompleted = " + previousCompleted);
        System.out.println("nextDateIsClose = " + nextDateIsClose);

        if (!previousCompleted && !nextDateIsClose) {
            System.out.println("Skip: previous not completed and next date is not close");
            return;
        }

        if (rule.getEndDate() != null && nextDate.isAfter(rule.getEndDate())) {
            System.out.println("Skip: after end date");
            return;
        }

        if (taskRepository.existsByRepeatRule_IdAndDueTime(rule.getId(), nextDate)) {
            System.out.println("Skip: task already exists");
            return;
        }

        System.out.println("Creating task for " + nextDate);
        createTaskForDate(rule, nextDate);
    }

    private void createTaskForDate(TaskRepeatRule rule, LocalDate dueDate) {
        List<Profile> candidates = getAvailableCandidates(rule);

        if (candidates.isEmpty()) {
            return;
        }

        Map<Long, Double> loads = taskLoadService.calculateProfileLoad(
                rule,
                candidates,
                dueDate.minusDays(LOAD_WINDOW_DAYS),
                dueDate.plusDays(LOAD_WINDOW_DAYS)
        );

        Profile assignedProfile = chooseAssignedProfile(candidates, loads);

        Task task = new Task(
                rule.getCreatedBy(),
                assignedProfile,
                rule.getName(),
                rule.getDescription(),
                rule.getRoom(),
                rule.getPriority(),
                rule.getPoints(),
                dueDate,
                rule
        );

        taskRepository.save(task);
    }

    private List<Profile> getAvailableCandidates(TaskRepeatRule rule) {
        return rule.getAssignedProfiles().stream()
                .filter(profile -> profile.getLeftAt() == null)
                .toList();
    }

    private Profile chooseAssignedProfile(
            List<Profile> candidates,
            Map<Long, Double> loads
    ) {
        return candidates.stream()
                .min(Comparator.comparingDouble(
                        profile -> loads.getOrDefault(profile.getId(), 0.0)
                ))
                .orElseThrow();
    }

}