package com.example.backend.dtos.in.buyings;

import com.example.backend.dtos.in.DtoValidationTest;
import com.example.backend.types.BuyingCategory;
import org.junit.jupiter.api.Test;

public class CreateBuyingDtoTest extends DtoValidationTest {

    @Test
    void shouldPassOnValidDto() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnNullAssignedTo() {
        CreateBuyingDto dto = new CreateBuyingDto(
                null,
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnTheBiggestLowerAssignedToValue() {
        CreateBuyingDto dto = new CreateBuyingDto(
                0L,
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinAssignedToValue() {
        CreateBuyingDto dto = new CreateBuyingDto(
                1L,
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnNullName() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                null,
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankName() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnTheShortestName() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "А",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnTheLongestName() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "А".repeat(64),
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinUpperLengthName() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "А".repeat(65),
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNullQuantity() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "Хлеб",
                null,
                BuyingCategory.BAKERY,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankQuantity() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "Хлеб",
                "",
                BuyingCategory.BAKERY,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnTheShortestQuantity() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "Хлеб",
                "А",
                BuyingCategory.BAKERY,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnTheLongestQuantity() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "Хлеб",
                "А".repeat(16),
                BuyingCategory.BAKERY,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinUpperLengthQuantity() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "Хлеб",
                "А".repeat(17),
                BuyingCategory.BAKERY,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNullCategory() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "Хлеб",
                "1 шт.",
                null,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNullIsPublic() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "Хлеб",
                "1 шт.",
                BuyingCategory.BAKERY,
                null
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnInvalidPublicAssignment() {
        CreateBuyingDto dto = new CreateBuyingDto(
                18L,
                "Хлеб",
                "1 шт.",
                BuyingCategory.BAKERY,
                false
        );

        assertHasViolations(dto);
    }

}
