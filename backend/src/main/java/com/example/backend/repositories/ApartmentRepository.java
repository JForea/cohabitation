package com.example.backend.repositories;

import com.example.backend.entities.Apartment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApartmentRepository extends ListCrudRepository<Apartment, Integer> {
    Optional<Apartment> findByInviteCode(String code);
    @Query("""
        UPDATE Apartment a
        SET a.inviteCode = :inviteCode
            WHERE a.id = :id
    """)
    void updateInviteCodeById(
            @Param("id") Integer id,
            @Param("inviteCode") String inviteCode
    );
    @Query("""
        UPDATE Apartment a
        SET a.budget = :budget
            WHERE a.id = :id
    """)
    void updateBudgetById(
            @Param("id") Integer id,
            @Param("budget") Integer budget
    );
}
