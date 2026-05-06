package com.example.backend.services;

import com.example.backend.dtos.in.rules.CreateRuleRequest;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.rules.RuleDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Rule;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    private final ApartmentRepository apartmentRepository;

    public RuleService(RuleRepository ruleRepository,
                       ApartmentRepository apartmentRepository) {
        this.ruleRepository = ruleRepository;
        this.apartmentRepository = apartmentRepository;
    }

    public IdResponse<Long> create(Integer apartmentId, CreateRuleRequest request) {
        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(
                () -> new ResourceNotFoundException("Apartment not found.")
        );

        Rule rule = new Rule(
                apartment,
                request.text()
        );

        rule = ruleRepository.save(rule);

        return new IdResponse<>(rule.getId());
    }

    public List<RuleDto> get(Integer apartmentId) {
        return ruleRepository.findAllByApartment_Id(apartmentId).stream().map(RuleDto::new).toList();
    }

    public void deleteOne(Long ruleId) {
        ruleRepository.deleteById(ruleId);
    }
}
