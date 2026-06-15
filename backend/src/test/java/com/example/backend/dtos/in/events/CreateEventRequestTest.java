package com.example.backend.dtos.in.events;

import com.example.backend.dtos.in.DtoValidationTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateEventRequestTest extends DtoValidationTest {

    @Test
    void shouldFailOnNullName() {
        CreateEventRequest dto = new CreateEventRequest(
                null,
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                "Важное собрание"
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankName() {
        CreateEventRequest dto = new CreateEventRequest(
                "",
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                "Важное собрание"
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinNameLength() {
        CreateEventRequest dto = new CreateEventRequest(
                "А",
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                "Важное собрание"
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxNameLength() {
        CreateEventRequest dto = new CreateEventRequest(
                "А".repeat(64),
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                "Важное собрание"
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinUpperInvalidLength() {
        CreateEventRequest dto = new CreateEventRequest(
                "А".repeat(65),
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                "Важное собрание"
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNullDate() {
        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                null,
                LocalTime.now(),
                "Важное собрание"
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnDateBeforeNow() {
        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                LocalDate.now().minusDays(1),
                LocalTime.now(),
                "Важное собрание"
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnDateNowAndTimeNull() {
        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                LocalDate.now(),
                null,
                "Важное собрание"
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnDateNowAndTimeBefore() {
        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                LocalDate.now(),
                LocalTime.now().minusMinutes(1),
                "Важное собрание"
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnDateNowAndTimeAfter() {
        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                LocalDate.now(),
                LocalTime.now().plusMinutes(1),
                "Важное собрание"
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnDateAfterAndTimeBefore() {
        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                LocalDate.now().plusDays(1),
                LocalTime.now().minusMinutes(1),
                "Важное собрание"
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnNullDescription() {
        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                null
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnBlankDescription() {
        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                ""
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxDescriptionLength() {
        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                "А".repeat(256)
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinDescriptionInvalidLength() {
        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                "А".repeat(257)
        );

        assertHasViolations(dto);
    }

}
