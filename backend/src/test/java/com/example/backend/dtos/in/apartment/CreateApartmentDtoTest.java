package com.example.backend.dtos.in.apartment;

import com.example.backend.dtos.in.DtoValidationTest;
import org.junit.jupiter.api.Test;

class CreateApartmentDtoTest extends DtoValidationTest {

    @Test
    void shouldPassOnValidDto() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "ул. Ленина, 42",
                (short)120
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnNullName() {
        CreateApartmentDto dto = new CreateApartmentDto(
                null,
                "ул. Ленина, 42",
                (short)120
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankName() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "",
                "ул. Ленина, 42",
                (short)120
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnTooShortName() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "А",
                "ул. Ленина, 42",
                (short)120
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinimalLengthName() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Аб",
                "ул. Ленина, 42",
                (short)120
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxLengthName() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "А".repeat(32),
                "ул. Ленина, 42",
                (short)120
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnTooLongName() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "А".repeat(33),
                "ул. Ленина, 42",
                (short)120
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnNullAddress() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                null,
                (short)120
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnBlankAddress() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "",
                (short)120
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxLengthAddress() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "А".repeat(64),
                (short)120
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnTooLongAddress() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "А".repeat(65),
                (short)120
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNullMinutesOffset() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "ул. Ленина, 42",
                null
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnTooBigLowerMinutesOffset() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "ул. Ленина, 42",
                (short)-721
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnTheBiggestLowerMinutesOffset() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "ул. Ленина, 42",
                (short)-720
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnTheBiggestUpperMinutesOffset() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "ул. Ленина, 42",
                (short)840
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnTooBigUpperMinutesOffset() {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "ул. Ленина, 42",
                (short)841
        );

        assertHasViolations(dto);
    }
}