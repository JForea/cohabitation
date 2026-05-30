package com.example.backend.validation.validators;

import com.example.backend.dtos.in.buyings.AssignableBuying;
import com.example.backend.validation.annotations.ValidPublicAssignment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PublicAssignmentValidator implements ConstraintValidator<ValidPublicAssignment, AssignableBuying> {

    @Override
    public boolean isValid(
            AssignableBuying value,
            ConstraintValidatorContext context
    ) {
        if (value == null || value.isPublic() == null)
            return true;

        if (value.assignedTo() == null)
            return true;

        return value.isPublic();
    }

}