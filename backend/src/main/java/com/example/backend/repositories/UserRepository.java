package com.example.backend.repositories;

import com.example.backend.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Integer> {
    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.currentProfile
        WHERE u.email = :email
    """)
    Optional<User> findByEmailWithProfile(String email);
    Optional<User> findByEmail(String email);
    @Modifying
    @Query("""
        UPDATE User u
        SET u.currentProfile = NULL
        WHERE u.id = :id
    """)
    void setCurrentProfileNullWhereId(Integer id);
}
