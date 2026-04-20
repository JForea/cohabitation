package com.example.backend.repositories;

import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface ProfileRepository extends ListCrudRepository<Profile, Long> {
    Optional<Profile> findByApartmentAndUser(Apartment apartment, User user);
    Optional<Profile> findByUserAndApartment_id(User user, Integer apartmentId);
}
