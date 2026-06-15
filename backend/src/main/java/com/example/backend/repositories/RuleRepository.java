package com.example.backend.repositories;

import com.example.backend.entities.Rule;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface RuleRepository extends ListCrudRepository<Rule, Long> {
    List<Rule> findAllByApartment_Id(Integer apartmentId);
}
