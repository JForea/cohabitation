package com.example.backend.repositories;

import com.example.backend.entities.MonthlyExpense;
import com.example.backend.entities.keys.MonthlyExpenseKey;
import org.springframework.data.repository.ListCrudRepository;

public interface MonthlyExpenseRepository extends ListCrudRepository<MonthlyExpense, MonthlyExpenseKey> {
}
