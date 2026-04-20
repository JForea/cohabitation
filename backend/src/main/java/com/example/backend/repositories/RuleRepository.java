package com.example.backend.repositories;

import com.example.backend.entities.Rule;
import org.springframework.data.repository.ListCrudRepository;

public interface RuleRepository extends ListCrudRepository<Rule, Long> {
}
