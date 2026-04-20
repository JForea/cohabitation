package com.example.backend.repositories;

import com.example.backend.entities.Expense;
import org.springframework.data.repository.ListCrudRepository;

public interface ExpenseRepository extends ListCrudRepository<Expense, Long> {
}
