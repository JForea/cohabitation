package com.example.backend.services;

import com.example.backend.entities.Profile;
import com.example.backend.entities.Task;
import com.example.backend.entities.TaskRepeatRule;
import com.example.backend.intefaces.ITaskLoadService;
import com.example.backend.intefaces.ITaskRepeatGenerationService;
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
public class TaskRepeatGenerationService implements ITaskRepeatGenerationService {

    private static final int LOAD_WINDOW_DAYS = 30;

    private final TaskRepeatRuleRepository taskRepeatRuleRepository;
    private final TaskRepository taskRepository;
    private final ITaskLoadService iTaskLoadService;

    public TaskRepeatGenerationService(
            TaskRepeatRuleRepository taskRepeatRuleRepository,
            TaskRepository taskRepository,
            ITaskLoadService iTaskLoadService
    ) {
        this.taskRepeatRuleRepository = taskRepeatRuleRepository;
        this.taskRepository = taskRepository;
        this.iTaskLoadService = iTaskLoadService;
    }

    @Transactional
    public void ensureNextTasksExist() {
        List<TaskRepeatRule> rules = taskRepeatRuleRepository.findAllByActiveTrue();

        for (TaskRepeatRule rule : rules) {
            ensureNextTaskExists(rule);
        }
    }

    private void ensureNextTaskExists(TaskRepeatRule rule) {
        if (rule == null || !Boolean.TRUE.equals(rule.getActive()))
            return;

        Optional<Task> lastTaskOptional =
                taskRepository.findTopByRepeatRule_IdOrderByDueTimeDesc(rule.getId());

        if (lastTaskOptional.isEmpty()) {
            createTaskForDate(rule, rule.getStartDate());
            return;
        }

        Task lastTask = lastTaskOptional.get();

        LocalDate nextDate = lastTask.getDueTime().plusDays(rule.getIntervalDays());

        boolean previousCompleted = lastTask.getCompletedAt() != null;
        boolean nextDateIsClose = !nextDate.isAfter(LocalDate.now().plusDays(1));

        if (!previousCompleted && !nextDateIsClose)
            return;

        if (rule.getEndDate() != null && nextDate.isAfter(rule.getEndDate()))
            return;

        if (taskRepository.existsByRepeatRule_IdAndDueTime(rule.getId(), nextDate))
            return;

        createTaskForDate(rule, nextDate);
    }

    private void createTaskForDate(TaskRepeatRule rule, LocalDate dueDate) {
        List<Profile> candidates = getAvailableCandidates(rule);

        Profile assignedProfile = null;

        if (!candidates.isEmpty()) {
            Map<Long, Double> loads = iTaskLoadService.calculateProfileLoad(
                    rule,
                    candidates,
                    dueDate.minusDays(LOAD_WINDOW_DAYS),
                    dueDate.plusDays(LOAD_WINDOW_DAYS)
            );

            assignedProfile = chooseAssignedProfile(candidates, loads);
        }

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