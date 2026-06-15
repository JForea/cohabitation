package com.example.backend.dtos.in.apartment;

import com.example.backend.dtos.in.DtoValidationTest;
import org.junit.jupiter.api.Test;

public class SetBudgetRequestTest extends DtoValidationTest {

    @Test
    void shouldPassOnValidDto() {
        SetBudgetRequest dto = new SetBudgetRequest(100000);

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnNullBudgetValue() {
        SetBudgetRequest dto = new SetBudgetRequest(null);

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnTooBigLowerBudgetValue() {
        SetBudgetRequest dto = new SetBudgetRequest(-1);

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnTheSmallestBudgetValue() {
        SetBudgetRequest dto = new SetBudgetRequest(0);

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnTheBiggestBudgetValue() {
        SetBudgetRequest dto = new SetBudgetRequest(1000000);

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnTooBigUpperBudgetValue() {
        SetBudgetRequest dto = new SetBudgetRequest(1000001);

        assertHasViolations(dto);
    }
}
