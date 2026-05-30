package com.example.backend.services;

import com.example.backend.dtos.in.rules.CreateRuleRequest;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.rules.RuleDto;
import com.example.backend.entities.Apartment;
import com.example.backend.entities.Rule;
import com.example.backend.entities.User;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.intefaces.RuleNotificationHandler;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.RuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleServiceTest {

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private ApartmentRepository apartmentRepository;

    @Mock
    private RuleNotificationHandler ruleNotificationHandler;

    @InjectMocks
    private RuleService ruleService;

    @Test
    void shouldCreateRule() {
        User user = mock(User.class);

        Apartment apartment = mock(Apartment.class);

        CreateRuleRequest request =
                new CreateRuleRequest("Не шуметь после 23:00");

        Rule savedRule = mock(Rule.class);

        when(apartmentRepository.findById(1)).thenReturn(Optional.of(apartment));

        when(ruleRepository.save(Mockito.<Rule>any())).thenReturn(savedRule);

        when(savedRule.getId()).thenReturn(10L);

        IdResponse<Long> result = ruleService.create(user, 1, request);

        assertEquals(10L, result.id());

        verify(apartmentRepository).findById(1);

        verify(ruleRepository).save(Mockito.<Rule>any());

        verify(ruleNotificationHandler).handleRuleUpdate(user, savedRule, true);
    }

    @Test
    void shouldThrowWhenApartmentNotFound() {
        User user = mock(User.class);

        CreateRuleRequest request =
                new CreateRuleRequest("No smoking");

        when(apartmentRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                ruleService.create(user, 1, request)
        );

        verify(ruleRepository, never()).save(any());

        verifyNoInteractions(ruleNotificationHandler);
    }

    @Test
    void shouldGetRules() {
        Rule rule1 = mock(Rule.class);
        Rule rule2 = mock(Rule.class);

        when(ruleRepository.findAllByApartment_Id(1))
                .thenReturn(List.of(rule1, rule2));

        List<RuleDto> result = ruleService.get(1);

        verify(ruleRepository)
                .findAllByApartment_Id(1);

        assertEquals(2, result.size());
    }

    @Test
    void shouldDeleteRule() {
        User user = mock(User.class);

        Rule rule = mock(Rule.class);

        when(ruleRepository.findById(1L))
                .thenReturn(Optional.of(rule));

        ruleService.deleteOne(user, 1L);

        verify(ruleRepository)
                .findById(1L);

        verify(ruleRepository)
                .deleteById(1L);

        verify(ruleNotificationHandler)
                .handleRuleUpdate(user, rule, false);
    }

    @Test
    void shouldThrowWhenRuleNotFound() {
        User user = mock(User.class);

        when(ruleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                ruleService.deleteOne(user, 1L)
        );

        verify(ruleRepository, never())
                .deleteById(anyLong());

        verifyNoInteractions(ruleNotificationHandler);
    }

}
