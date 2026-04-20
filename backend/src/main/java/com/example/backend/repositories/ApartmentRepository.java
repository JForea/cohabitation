package com.example.backend.repositories;

import com.example.backend.entities.Apartment;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface ApartmentRepository extends ListCrudRepository<Apartment, Integer> {
    Optional<Apartment> findByInviteCode(String code);
}
