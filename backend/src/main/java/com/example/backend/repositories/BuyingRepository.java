package com.example.backend.repositories;

import com.example.backend.entities.Buying;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface BuyingRepository extends ListCrudRepository<Buying, Long>, JpaSpecificationExecutor<Buying> {
    Optional<Buying> findByCreatedBy_Apartment_IdAndId(Integer apartmentId, Long id);
    List<Buying> findAllByCreatedBy_Apartment_IdAndIdIn(Integer apartmentId, List<Long> ids);
    void deleteAllByIdIn(List<Long> ids);
}
