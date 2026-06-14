package com.example.backend.schedulers;

import com.example.backend.intefaces.ITaskRepeatGenerationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskRepeatScheduler {

    private final ITaskRepeatGenerationService iTaskRepeatGenerationService;

    public TaskRepeatScheduler(ITaskRepeatGenerationService iTaskRepeatGenerationService) {
        this.iTaskRepeatGenerationService = iTaskRepeatGenerationService;
    }

    @Scheduled(cron = "0 1 0 * * *")
//    @Scheduled(fixedRate = 10000)
    public void generateRepeatedTasks() {
        iTaskRepeatGenerationService.ensureNextTasksExist();
    }
}