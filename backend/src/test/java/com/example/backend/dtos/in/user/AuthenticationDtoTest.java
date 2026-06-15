package com.example.backend.dtos.in.user;

import com.example.backend.dtos.in.DtoValidationTest;
import com.example.backend.dtos.in.tokens.DeviceTokenDto;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class AuthenticationDtoTest extends DtoValidationTest {

    @Test
    void shouldFailOnNullEmail() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                null,
                "password",
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankEmail() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "",
                "password",
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnNotEmail() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "aaaaaaaaa",
                "password",
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnTooLongEmail() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "a".repeat(64) + "@"
                        + "b".repeat(63) + "."
                        + "c".repeat(63) + "."
                        + "d".repeat(61) + ".a",
                "password",
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMaxEmailValidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "a".repeat(64) + "@"
                        + "b".repeat(63) + "."
                        + "c".repeat(63) + "."
                        + "d".repeat(60) + ".a",
                "password",
                deviceTokenDto
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnValidEmail() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "password",
                deviceTokenDto
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnNullPassword() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                null,
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankPassword() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "",
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnMaxPasswordLowerInvalidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "a".repeat(7),
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnMinPasswordUpperInvalidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "a".repeat(33),
                deviceTokenDto
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinPasswordValidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "a".repeat(8),
                deviceTokenDto
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxPasswordValidLength() {
        DeviceTokenDto deviceTokenDto = mock(DeviceTokenDto.class);

        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "a".repeat(32),
                deviceTokenDto
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnNullDeviceToken() {
        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "password",
                null
        );

        assertHasNoViolations(dto);
    }

}
