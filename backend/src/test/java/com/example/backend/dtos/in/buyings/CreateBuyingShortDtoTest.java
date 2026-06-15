package com.example.backend.dtos.in.buyings;

import com.example.backend.dtos.in.DtoValidationTest;
import com.example.backend.types.BuyingCategory;
import org.junit.jupiter.api.Test;

public class CreateBuyingShortDtoTest extends DtoValidationTest {

    @Test
    void shouldPassOnValidDto() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnNullName() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                null,
                "1 шт",
                BuyingCategory.BAKERY
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankName() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "",
                "1 шт",
                BuyingCategory.BAKERY
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnTheShortestName() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "А",
                "1 шт",
                BuyingCategory.BAKERY
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnTheLongestName() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "А".repeat(64),
                "1 шт",
                BuyingCategory.BAKERY
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinUpperLengthName() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "А".repeat(65),
                "1 шт",
                BuyingCategory.BAKERY
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNullQuantity() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "Хлеб",
                null,
                BuyingCategory.BAKERY
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankQuantity() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "Хлеб",
                "",
                BuyingCategory.BAKERY
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnTheShortestQuantity() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "Хлеб",
                "А",
                BuyingCategory.BAKERY
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnTheLongestQuantity() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "Хлеб",
                "А".repeat(16),
                BuyingCategory.BAKERY
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinUpperLengthQuantity() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "Хлеб",
                "А".repeat(17),
                BuyingCategory.BAKERY
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNullCategory() {
        CreateBuyingShortDto dto = new CreateBuyingShortDto(
                "Хлеб",
                "1 шт.",
                null
        );

        assertHasViolations(dto);
    }
}
