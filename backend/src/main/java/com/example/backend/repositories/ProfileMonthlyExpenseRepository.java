package com.example.backend.repositories;

import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.ProfileMonthlyExpense;
import com.example.backend.types.ExpenseCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Month;
import java.util.Optional;

public interface ProfileMonthlyExpenseRepository extends ListCrudRepository<ProfileMonthlyExpense, Long> {
    @Query("""
        SELECT sum(e.amount)
        FROM ProfileMonthlyExpense e
        WHERE e.profile.apartment = :apartment
            AND e.year = :year
            AND e.month = :month
    """)
    Integer getSumByApartmentAndYearAndMonth(
            @Param("apartment") Apartment apartment,
            @Param("year") Integer year,
            @Param("month") Month month
    );
    @Query("""
        SELECT sum(e.amount)
        FROM ProfileMonthlyExpense e
        WHERE e.profile.apartment.id = :apartmentId
            AND e.year = :year
            AND e.month = :month
    """)
    Integer getSumByApartmentIdAndYearAndMonth(
            @Param("apartmentId") Integer apartmentId,
            @Param("year") Integer year,
            @Param("month") Month month
    );
    @Query("""
        SELECT sum(e.amount)
        FROM ProfileMonthlyExpense e
        WHERE e.profile = :profile
            AND e.year = :year
            AND e.month = :month
    """)
    Integer getSumByProfileAndYearAndMonth(
            @Param("profile") Profile profile,
            @Param("year") Integer year,
            @Param("month") Month month
    );
    Optional<ProfileMonthlyExpense> findByYearAndMonthAndProfileAndExpenseCategory(
            Integer year,
            Month month,
            Profile profile,
            ExpenseCategory category
    );
}
