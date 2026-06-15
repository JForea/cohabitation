package com.example.backend.intefaces;

import com.example.backend.entities.Expense;
import com.example.backend.entities.User;

import java.util.List;

public interface ExpenseNotificationHandler {
    void handleExpenseCreate(User createdBy, Expense expense);
    void handleManyExpensesDelete(User createdBy, List<Expense> expenses);
}
