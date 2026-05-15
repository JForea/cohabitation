package com.example.backend.repositories;

import com.example.backend.entities.Expense;
import com.example.backend.types.BuyingCategory;
import com.example.backend.types.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;


public interface ExpenseRepository extends ListCrudRepository<Expense, Long> {
    Page<Expense> findAllByCreatedBy_Apartment_Id(Integer apartmentId, Pageable pageable);
    List<Expense> findAllByCreatedBy_Apartment_IdAndIdIn(Integer apartmentId, List<Long> taskIds);
    @Query("""
        SELECT sum(e.amount)
        FROM Expense e
        WHERE e.apartment.id = :apartmentId
            AND e.category = :category
            AND e.createdAt >= :start
            AND e.createdAt < :end
    """)
    Integer getAmountSumByApartmentIdAndCategoryAndCreatedAtBetween(
            @Param("apartmentId") Integer apartmentId,
            @Param("category") ExpenseCategory category,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}
