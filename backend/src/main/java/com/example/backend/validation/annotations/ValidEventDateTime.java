package com.example.backend.validation.annotations;

import com.example.backend.validation.validators.FutureEventValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureEventValidator.class)
public @interface ValidEventDateTime {

    String message() default "Event datetime must be in the future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
