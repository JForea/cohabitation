package com.example.backend.repositories;

import com.example.backend.entities.Buying;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface BuyingRepository extends ListCrudRepository<Buying, Long>, JpaSpecificationExecutor<Buying> {
    List<Buying> findAllByApartment_IdOrderByCategory(Integer apartmentId, Specification<Buying> spec);
}
