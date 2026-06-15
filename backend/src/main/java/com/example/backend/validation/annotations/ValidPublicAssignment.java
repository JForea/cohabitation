package com.example.backend.validation.annotations;

import com.example.backend.validation.validators.PublicAssignmentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PublicAssignmentValidator.class)
public @interface ValidPublicAssignment {

    String message() default
            "Assigned buying must be public.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
