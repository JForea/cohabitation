package com.example.backend.intefaces;

import com.example.backend.entities.Expense;
import com.example.backend.entities.User;

public interface ExpenseNotificationHandler {
    void handleExpenseNotification(User createdBy, Expense expense, boolean creating);
}
