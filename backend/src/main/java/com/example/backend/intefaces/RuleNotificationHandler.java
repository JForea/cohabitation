package com.example.backend.intefaces;

import com.example.backend.entities.Rule;
import com.example.backend.entities.User;

public interface RuleNotificationHandler {
    void handleRuleNotification(User createdBy, Rule rule, boolean creating);
}
