package com.example.backend.dtos.in;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class DtoValidationTest {

    protected Validator validator;

    @BeforeEach
    void setUpValidator() {
        ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory();

        validator = factory.getValidator();
    }

    protected <T> Set<ConstraintViolation<T>> validate(T dto) {
        return validator.validate(dto);
    }

    protected <T> void assertHasViolations(T dto) {
        assertFalse(validate(dto).isEmpty());
    }

    protected <T> void assertHasNoViolations(T dto) {
        assertTrue(validate(dto).isEmpty());
    }

}
