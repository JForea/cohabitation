package com.example.backend.dtos.in.user;

import com.example.backend.dtos.in.DtoValidationTest;
import org.junit.jupiter.api.Test;

public class LogoutRequestTest extends DtoValidationTest {

    @Test
    void shouldFailOnNullDeviceToken() {
        LogoutRequest dto = new LogoutRequest(null);

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnValidDto() {
        LogoutRequest dto = new LogoutRequest("deviceId");

        assertHasNoViolations(dto);
    }

}
