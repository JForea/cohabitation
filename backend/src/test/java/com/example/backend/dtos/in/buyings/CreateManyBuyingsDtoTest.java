package com.example.backend.dtos.in.buyings;

import com.example.backend.dtos.in.DtoValidationTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class CreateManyBuyingsDtoTest extends DtoValidationTest {

    @Test
    void shouldFailOnNullBuyings() {
        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                null,
                1L,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnEmptyBuyings() {
        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(),
                1L,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassMinBuyingsSize() {
        CreateBuyingShortDto createBuyingShortDto = mock(CreateBuyingShortDto.class);

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(createBuyingShortDto),
                1L,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassMaxBuyingsSize() {
        CreateBuyingShortDto createBuyingShortDto = mock(CreateBuyingShortDto.class);

        List<CreateBuyingShortDto> buyings = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            buyings.add(createBuyingShortDto);
        }

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                buyings,
                1L,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailMinUpperBuyingsSize() {
        CreateBuyingShortDto createBuyingShortDto = mock(CreateBuyingShortDto.class);

        List<CreateBuyingShortDto> buyings = new ArrayList<>();

        for (int i = 0; i < 51; i++) {
            buyings.add(createBuyingShortDto);
        }

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                buyings,
                1L,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnNullAssignedTo() {
        CreateBuyingShortDto createBuyingShortDto = mock(CreateBuyingShortDto.class);

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(createBuyingShortDto),
                null,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnTheBiggestLowerAssignedToValue() {
        CreateBuyingShortDto createBuyingShortDto = mock(CreateBuyingShortDto.class);

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(createBuyingShortDto),
                0L,
                true
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinAssignedToValue() {
        CreateBuyingShortDto createBuyingShortDto = mock(CreateBuyingShortDto.class);

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(createBuyingShortDto),
                1L,
                true
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnNullIsPublic() {
        CreateBuyingShortDto createBuyingShortDto = mock(CreateBuyingShortDto.class);

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(createBuyingShortDto),
                1L,
                null
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnInvalidAssignment() {
        CreateBuyingShortDto createBuyingShortDto = mock(CreateBuyingShortDto.class);

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(createBuyingShortDto),
                1L,
                false
        );

        assertHasViolations(dto);
    }

}
