package com.example.backend.repositories;

import com.example.backend.entities.Buying;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface BuyingRepository extends ListCrudRepository<Buying, Long>, JpaSpecificationExecutor<Buying> {
    List<Buying> findAllByApartment_IdOrderByCategory(Integer apartmentId, Specification<Buying> spec);
    Optional<Buying> findByApartment_IdAndId(Integer apartmentId, Long id);
}
