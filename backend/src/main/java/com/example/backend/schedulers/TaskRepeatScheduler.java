package com.example.backend.schedulers;

import com.example.backend.services.TaskRepeatGenerationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskRepeatScheduler {

    private final TaskRepeatGenerationService taskRepeatGenerationService;

    public TaskRepeatScheduler(TaskRepeatGenerationService taskRepeatGenerationService) {
        this.taskRepeatGenerationService = taskRepeatGenerationService;
    }

    @Scheduled(cron = "0 1 0 * * *")
//    @Scheduled(fixedRate = 10000)
    public void generateRepeatedTasks() {
        taskRepeatGenerationService.ensureNextTasksExist();
    }
}