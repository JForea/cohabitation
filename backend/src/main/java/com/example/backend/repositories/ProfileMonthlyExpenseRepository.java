package com.example.backend.repositories;

import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.ProfileMonthlyExpense;
import com.example.backend.entities.keys.ProfileMonthlyExpenseKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Month;
import java.util.Optional;

public interface ProfileMonthlyExpenseRepository extends ListCrudRepository<ProfileMonthlyExpense, ProfileMonthlyExpenseKey> {
    @Query("""
        SELECT sum(e.amount)
        FROM ProfileMonthlyExpense e
        WHERE e.profileMonthlyExpenseKey.profile.apartment = :apartment
            AND e.profileMonthlyExpenseKey.month = :month
    """)
    Integer getSumByApartmentAndMonth(@Param("apartment") Apartment apartment, @Param("month") Month month);
    Optional<ProfileMonthlyExpense> findByProfileMonthlyExpenseKey_ProfileAndProfileMonthlyExpenseKey_Month(
            Profile profile,
            Month month
    );
}
