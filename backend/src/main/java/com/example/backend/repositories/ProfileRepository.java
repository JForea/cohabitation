package com.example.backend.repositories;

import com.example.backend.entities.Apartment;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends ListCrudRepository<Profile, Long> {
    Optional<Profile> findByApartmentAndUser(Apartment apartment, User user);
    Optional<Profile> findByUserAndApartment_id(User user, Integer apartmentId);
    List<Profile> findAllByApartment_IdAndUserNot(Integer apartmentId, User user);
    List<Profile> findAllByApartment_IdAndIdNot(Integer apartmentId, Long profileId);
    List<Profile> findAllByApartment_Id(Integer apartmentId);
}
