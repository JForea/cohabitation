package com.example.backend.repositories;

import com.example.backend.entities.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);
}
