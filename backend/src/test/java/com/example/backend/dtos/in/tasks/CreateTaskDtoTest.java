package com.example.backend.dtos.in.tasks;

import com.example.backend.dtos.in.DtoValidationTest;
import com.example.backend.types.Room;
import com.example.backend.types.TaskPriority;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class CreateTaskDtoTest extends DtoValidationTest {

    @Test
    void shouldFailOnNullName() {
        CreateTaskDto dto = new CreateTaskDto(
                null,
                "Убраться повсюду",
                18L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankName() {
        CreateTaskDto dto = new CreateTaskDto(
                "",
                "Убраться повсюду",
                18L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnMaxNameInvalidLowerLengthLimit() {
        CreateTaskDto dto = new CreateTaskDto(
                "АА",
                "Убраться повсюду",
                18L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinNameValidLength() {
        CreateTaskDto dto = new CreateTaskDto(
                "ААА",
                "Убраться повсюду",
                18L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxNameValidLength() {
        CreateTaskDto dto = new CreateTaskDto(
                "А".repeat(64),
                "Убраться повсюду",
                18L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinNameUpperInvalidLength() {
        CreateTaskDto dto = new CreateTaskDto(
                "А".repeat(65),
                "Убраться повсюду",
                18L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnNullDescription() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                null,
                18L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnBlankDescription() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "",
                18L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxDescriptionValidLength() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "А".repeat(256),
                18L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinDescriptionUpperInvalidLength() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "А".repeat(257),
                18L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnNullAssignedTo() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                null,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMaxAssignedToInvalidValue() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                0L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnNullRoom() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                null,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnNullPriority() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                null,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnNullPoints() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                null,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMaxInvalidPoints() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                0L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                null,
                (short)7,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnMaxLowerPointsInvalidValue() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)-1,
                (short)7,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnMinUpperPointsInvalidValue() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)101,
                (short)7,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinPointsValidValue() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)0,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxPointsValidValue() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)100,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnNullRepeatTime() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                null,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMaxLowerRepeatTimeInvalidValue() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)0,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnMinUpperRepeatTimeInvalidValue() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)366,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinRepeatTimeValidValue() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)1,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxRepeatTimeValidValue() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)365,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinRepeatTimeUpperInvalidValue() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)366,
                LocalDate.now()
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnNullDueDate() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                null
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnDueDateBeforeNow() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now().minusDays(1)
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnDueDateNow() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now()
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnDueDateAfterNow() {
        CreateTaskDto dto = new CreateTaskDto(
                "Уборка",
                "Убраться повсюду",
                1L,
                Room.COMMON,
                TaskPriority.MEDIUM,
                (short)5,
                (short)7,
                LocalDate.now().plusDays(1)
        );

        assertHasNoViolations(dto);
    }

}
