package com.example.backend.services;

import com.example.backend.dtos.inner.TokenDto;
import com.example.backend.entities.Task;
import com.example.backend.entities.User;
import com.example.backend.intefaces.IPushNotificationService;
import com.example.backend.repositories.TaskRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Transactional
public class TaskReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(TaskReminderScheduler.class);
    private final TaskRepository taskRepository;

    private final IPushNotificationService iPushNotificationService;

    public TaskReminderScheduler(TaskRepository taskRepository,
                                 IPushNotificationService iPushNotificationService) {
        this.taskRepository = taskRepository;
        this.iPushNotificationService = iPushNotificationService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void sendTaskReminders() {
        List<Task> tasks = taskRepository.findAllForReminder();

        Set<Integer> userIds = new HashSet<>();
        List<TokenDto> tokens = new ArrayList<>();

        int remindingTime = 8 * 60;

        for (Task task : tasks) {
            short minutesOffset =
                    task.getAssignedTo()
                            .getApartment()
                            .getMinutesOffset();

            ZoneOffset offset =
                    ZoneOffset.ofTotalSeconds(
                            minutesOffset * 60
                    );

            OffsetDateTime localNow =
                    OffsetDateTime.now(offset);

            LocalDate localDate =
                    localNow.toLocalDate();

            int localMinutes =
                    localNow.toLocalTime()
                            .toSecondOfDay() / 60;

            if (task.getDueTime().equals(localDate) &&
                    !Objects.equals(localDate, task.getLastReminderDate()) &&
                    localMinutes >= remindingTime) {
                User user = task.getAssignedTo().getUser();

                if (userIds.add(user.getId()))
                    tokens.addAll(user.getDeviceTokens().stream().map(TokenDto::new).toList());

                task.setLastReminderDate(localDate);
            }
        }

        log.info("SCHEDULER IS WORKING: %s notifications are sending.".formatted(tokens.size()));

        if (!tokens.isEmpty()) {
            iPushNotificationService.send(
                    tokens,
                    "Предстоящие задачи",
                    "У вас есть невыполненные задачи на сегодня."
            );
        }
    }
}
