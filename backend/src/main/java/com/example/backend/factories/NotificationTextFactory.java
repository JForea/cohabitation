package com.example.backend.factories;

import com.example.backend.intefaces.INotificationTextFactory;
import com.example.backend.types.NotificationType;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationTextFactory implements INotificationTextFactory {
    @Override
    public String getTitle(NotificationType type) {
        return switch (type) {
            case TASK_CREATED -> "Новая задача";
            case TASK_DONE -> "Задача выполнена";
            case TASK_REOPENED -> "Задача открыта снова";
        };
    }

    public String getBody(NotificationType type, Map<String, Object> payload, boolean isPersonal) {
        switch (type) {
            case TASK_CREATED:
                String template;
                if (isPersonal)
                    template = "%s назначил вам задачу «%s».";
                else
                    template = "%s создал задачу «%s».";

                return template.formatted(payload.get("userName"), payload.get("taskName"));

            case TASK_DONE:
                return "%s завершил задачу «%s»".formatted(payload.get("userName"), payload.get("taskName"));

            case TASK_REOPENED:
                return "%s отметил задачу «%s» незавершённой".formatted(payload.get("userName"), payload.get("taskName"));
        }

        throw new NotImplementedException("Unknown notification type met during preparing notification text.");
    }
}
