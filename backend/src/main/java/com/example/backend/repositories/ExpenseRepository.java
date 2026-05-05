package com.example.backend.repositories;

import com.example.backend.entities.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;


public interface ExpenseRepository extends ListCrudRepository<Expense, Long> {
    Page<Expense> findAllByApartment_Id(Integer apartmentId, Pageable pageable);
}
