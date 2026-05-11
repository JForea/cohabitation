package com.example.backend.factories;

import com.example.backend.intefaces.INotificationTextFactory;
import com.example.backend.types.NotificationType;
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
            case TASK_DELETED -> "Задача отменена";
            case BUYING_UPDATED -> "Обновлён список покупок";
            case EXPENSE_CREATED -> "Добавлена запись о расходах";
            case EXPENSE_DELETED -> "Удалена запись о расходах";
            case RULE_CREATED -> "Добавлено правило";
            case RULE_DELETED -> "Удалено правило";
            case USER_JOINED -> "Пользователь присоединился";
            case USER_REJOINED -> "Пользователь вернулся";
            case USER_CREATOR -> "Пользователь стал новым владельцем";
            case USER_ADMIN -> "Пользователь стал администратором";
            case USER_INHABITANT -> "Администратор стал пользователем";
            case USER_LEFT -> "Пользователь вышел";
            case USER_KICK -> "Пользователя выгнали";
            case EVENT_CREATED -> "Добавлено событие";
            case EVENT_DELETED -> "Удалено событие";
        };
    }

    public String getBody(NotificationType type, Map<String, Object> payload, boolean isPersonal, boolean male) {
        String suffix = male ? "" : "а";
        String template;

        return switch (type) {
            case TASK_CREATED -> {
                if (isPersonal)
                    template = "%s назначил%s вам задачу «%s».";
                else
                    template = "%s назначил%s задачу «%s».";

                yield template.formatted(
                        payload.get("userName"),
                        suffix,
                        payload.get("taskName")
                );
            }
            case TASK_DONE -> "%s завершил%s задачу «%s»."
                    .formatted(
                            payload.get("userName"),
                            suffix,
                            payload.get("taskName")
                    );
            case TASK_REOPENED -> "%s отметил%s задачу «%s» незавершённой."
                    .formatted(
                            payload.get("userName"),
                            suffix,
                            payload.get("taskName")
                    );
            case TASK_DELETED -> "%s удалил%s задачу «%s»."
                    .formatted(
                            payload.get("userName"),
                            suffix,
                            payload.get("taskName")
                    );
            case BUYING_UPDATED -> {
                if (isPersonal)
                    template = "%s обновил%s ваш список покупок.";
                else
                    template = "%s обновил%s список покупок.";

                yield template.formatted(
                        payload.get("userName"),
                        suffix
                );
            }
            case EXPENSE_CREATED -> "%s добавил%s запись о расходах «%s» на сумму %s."
                    .formatted(
                            payload.get("userName"),
                            suffix,
                            payload.get("expenseName"),
                            payload.get("expenseAmount")
                    );
            case EXPENSE_DELETED -> "%s удалил%s запись о расходах «%s»."
                    .formatted(
                            payload.get("userName"),
                            suffix,
                            payload.get("expenseName")
                    );
            case RULE_CREATED -> "%s добавил%s правило «%s»."
                    .formatted(
                            payload.get("userName"),
                            suffix,
                            payload.get("ruleText")
                    );
            case RULE_DELETED -> "%s удалил%s правило «%s»."
                    .formatted(
                            payload.get("userName"),
                            suffix,
                            payload.get("ruleText")
                    );
            case USER_JOINED -> "%s присоединил%s."
                    .formatted(
                            payload.get("userName"),
                            male ? "ся" : "ась"
                    );
            case USER_REJOINED -> "%s вернул%s."
                    .formatted(
                            payload.get("userName"),
                            male ? "ся" : "ась"
                    );
            case USER_CREATOR -> {
                if (isPersonal)
                    yield "Вам переданы права владельца квартиры.";
                else
                    yield "%s стал%s новым владельцем квартиры.".formatted(
                            payload.get("userName"),
                            suffix
                    );
            }
            case USER_ADMIN -> {
                if (isPersonal)
                    yield "Вы назначены администратором.";
                else
                    yield "%s назначен%s администратором."
                    .formatted(
                            payload.get("userName"),
                            suffix
                    );
            }
            case USER_INHABITANT -> {
                if (isPersonal)
                    yield "Вы больше не администратор.";
                else
                    yield "%s больше не администратор."
                        .formatted(
                                payload.get("userName")
                        );
            }
            case USER_LEFT -> "%s покинул%s квартиру."
                    .formatted(
                            payload.get("userName"),
                            suffix
                    );
            case USER_KICK -> "%s выгнал%s %s"
                    .formatted(
                            payload.get("userName"),
                            suffix,
                            payload.get("targetUserName")
                    );
            case EVENT_CREATED -> "%s создал%s событие «%s»."
                    .formatted(
                            payload.get("userName"),
                            suffix,
                            payload.get("eventName")
                    );
            case EVENT_DELETED -> "%s удалил%s событие «%s»."
                    .formatted(
                            payload.get("userName"),
                            suffix,
                            payload.get("eventName")
                    );
            default -> "";
        };
    }
}
