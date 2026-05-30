package com.example.backend.validation.validators;

import com.example.backend.validation.annotations.ValidEventDateTime;
import com.example.backend.dtos.in.events.CreateEventRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FutureEventValidator implements ConstraintValidator<ValidEventDateTime, CreateEventRequest> {

    @Override
    public boolean isValid(
            CreateEventRequest value,
            ConstraintValidatorContext context
    ) {

        if (value == null)
            return true;

        if (value.date() == null)
            return true;

        if (value.time() == null)
            return !value.date().isBefore(LocalDate.now());

        LocalDateTime eventDateTime = LocalDateTime.of(value.date(), value.time());

        return eventDateTime.isAfter(LocalDateTime.now());
    }

}