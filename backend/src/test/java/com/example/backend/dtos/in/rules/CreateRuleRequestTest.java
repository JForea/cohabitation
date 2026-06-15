package com.example.backend.dtos.in.rules;

import com.example.backend.dtos.in.DtoValidationTest;
import org.junit.jupiter.api.Test;

public class CreateRuleRequestTest extends DtoValidationTest {

    @Test
    void shouldFailOnNullText() {
        CreateRuleRequest dto = new CreateRuleRequest(null);

        assertHasViolations(dto);
    }

    @Test
    void shouldFailOnBlankText() {
        CreateRuleRequest dto = new CreateRuleRequest("");

        assertHasViolations(dto);
    }

    @Test
    void shouldPassOnMinTextValidLength() {
        CreateRuleRequest dto = new CreateRuleRequest("А");

        assertHasNoViolations(dto);
    }

    @Test
    void shouldPassOnMaxTextValidLength() {
        CreateRuleRequest dto = new CreateRuleRequest("А".repeat(255));

        assertHasNoViolations(dto);
    }

    @Test
    void shouldFailOnMinTextInvalidLength() {
        CreateRuleRequest dto = new CreateRuleRequest("А".repeat(256));

        assertHasViolations(dto);
    }

}
