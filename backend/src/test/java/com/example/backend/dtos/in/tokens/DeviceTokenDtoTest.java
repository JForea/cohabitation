package com.example.backend.dtos.in.tokens;

import com.example.backend.dtos.in.DtoValidationTest;
import com.example.backend.types.Platform;
import org.junit.jupiter.api.Test;

public class DeviceTokenDtoTest extends DtoValidationTest {

    @Test
    void shouldFailOnNullDeviceId() {
        DeviceTokenDto dto = new DeviceTokenDto(
                null,
                "fcmToken",
                Platform.ANDROID
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankDeviceId() {
        DeviceTokenDto dto = new DeviceTokenDto(
                "",
                "fcmToken",
                Platform.ANDROID
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnTooLongDeviceId() {
        DeviceTokenDto dto = new DeviceTokenDto(
                "A".repeat(256),
                "fcmToken",
                Platform.ANDROID
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinDeviceIdValidLength() {
        DeviceTokenDto dto = new DeviceTokenDto(
                "A",
                "fcmToken",
                Platform.WEB
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxDeviceIdValidLength() {
        DeviceTokenDto dto = new DeviceTokenDto(
                "A".repeat(255),
                "fcmToken",
                Platform.IOS
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnNullToken() {
        DeviceTokenDto dto = new DeviceTokenDto(
                "uniqueDeviceId",
                null,
                Platform.ANDROID
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankToken() {
        DeviceTokenDto dto = new DeviceTokenDto(
                "uniqueDeviceId",
                "",
                Platform.ANDROID
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnTooLongToken() {
        DeviceTokenDto dto = new DeviceTokenDto(
                "uniqueDeviceId",
                "A".repeat(2049),
                Platform.ANDROID
        );

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinTokenValidLength() {
        DeviceTokenDto dto = new DeviceTokenDto(
                "uniqueDeviceId",
                "A",
                Platform.ANDROID
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxTokenValidLength() {
        DeviceTokenDto dto = new DeviceTokenDto(
                "uniqueDeviceId",
                "A".repeat(2048),
                Platform.ANDROID
        );

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnNullPlatform() {
        DeviceTokenDto dto = new DeviceTokenDto(
                "uniqueDeviceId",
                "fcmToken",
                null
        );

        assertHasViolations(dto);
    }

}
