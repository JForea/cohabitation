package com.example.backend.dtos.in.expenses;

import com.example.backend.dtos.in.DtoValidationTest;
import com.example.backend.types.ExpenseCategory;
import org.junit.jupiter.api.Test;

public class CreateExpenseRequestTest extends DtoValidationTest {

    @Test
    void shouldFailOnNullName() {
        CreateExpenseRequest dto = new CreateExpenseRequest(
                null,
                1000,
                ExpenseCategory.PRODUCTS
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankName() {
        CreateExpenseRequest dto = new CreateExpenseRequest(
                "",
                1000,
                ExpenseCategory.PRODUCTS
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMaxNameLength() {
        CreateExpenseRequest dto = new CreateExpenseRequest(
                "А".repeat(64),
                1000,
                ExpenseCategory.PRODUCTS
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinUpperNameInvalidLength() {
        CreateExpenseRequest dto = new CreateExpenseRequest(
                "А".repeat(65),
                1000,
                ExpenseCategory.PRODUCTS
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNullAmount() {
        CreateExpenseRequest dto = new CreateExpenseRequest(
                "Продукты",
                null,
                ExpenseCategory.PRODUCTS
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnMaxLowerAmountInvalidValue() {
        CreateExpenseRequest dto = new CreateExpenseRequest(
                "Продукты",
                0,
                ExpenseCategory.PRODUCTS
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinAmountValidValue() {
        CreateExpenseRequest dto = new CreateExpenseRequest(
                "Продукты",
                1,
                ExpenseCategory.PRODUCTS
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxAmountValidValue() {
        CreateExpenseRequest dto = new CreateExpenseRequest(
                "Продукты",
                1000000,
                ExpenseCategory.PRODUCTS
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinAmountInvalidValue() {
        CreateExpenseRequest dto = new CreateExpenseRequest(
                "Продукты",
                1000001,
                ExpenseCategory.PRODUCTS
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNullCategory() {
        CreateExpenseRequest dto = new CreateExpenseRequest(
                "Продукты",
                1000,
                null
        );

        assertHasViolations(dto);
    }

}
