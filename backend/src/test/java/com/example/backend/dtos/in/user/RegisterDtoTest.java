package com.example.backend.dtos.in.user;

import com.example.backend.dtos.in.DtoValidationTest;
import com.example.backend.dtos.in.tokens.DeviceTokenDto;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class RegisterDtoTest extends DtoValidationTest {

    @Test
    void shouldFailOnNullEmail() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                null,
                "password",
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankEmail() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "",
                "password",
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNotEmail() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "aaaaaaaaa",
                "password",
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnTooLongEmail() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "a".repeat(64) + "@"
                        + "b".repeat(63) + "."
                        + "c".repeat(63) + "."
                        + "d".repeat(61) + ".a",
                "password",
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMaxEmailValidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "a".repeat(64) + "@"
                        + "b".repeat(63) + "."
                        + "c".repeat(63) + "."
                        + "d".repeat(60) + ".a",
                "password",
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnValidEmail() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnNullPassword() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                null,
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankPassword() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "",
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnMaxPasswordLowerInvalidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "a".repeat(7),
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnMinPasswordUpperInvalidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "a".repeat(33),
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinPasswordValidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "a".repeat(8),
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxPasswordValidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "a".repeat(32),
                "Марк",
                true,
                deviceTokenDto
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnNullDeviceToken() {
        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                "Марк",
                true,
                null
        );

        assertHasNoViolations(dto);
    }
    
    @Test
    void shouldFailOnNullName() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                null,
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankName() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                "",
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailTooLongName() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                "a".repeat(41),
                true,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinNameValidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                "a",
                true,
                deviceTokenDto
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxNameValidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                "a".repeat(40),
                true,
                deviceTokenDto
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnNullMale() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                "Марк",
                null,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

}
