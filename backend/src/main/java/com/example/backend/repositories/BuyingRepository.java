package com.example.backend.repositories;

import com.example.backend.entities.Buying;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;

public interface BuyingRepository extends ListCrudRepository<Buying, Long>, JpaSpecificationExecutor<Buying> {
}
