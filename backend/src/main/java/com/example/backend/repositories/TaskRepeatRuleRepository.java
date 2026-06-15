package com.example.backend.repositories;

import com.example.backend.entities.TaskRepeatRule;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface TaskRepeatRuleRepository extends ListCrudRepository<TaskRepeatRule, Long> {
    List<TaskRepeatRule> findAllByActiveTrue();
    List<TaskRepeatRule> findAllByCreatedBy_Apartment_IdAndActiveTrue(Integer apartmentId);
}
