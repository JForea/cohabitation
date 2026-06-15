package com.example.backend.dtos.in.buyings;

import com.example.backend.types.BuyingCategory;
import com.example.backend.validation.annotations.ValidPublicAssignment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ValidPublicAssignment
public record CreateBuyingDto(
        @Min(1)
        Long assignedTo,
        @NotBlank(message = "Name must be provided.")
        @Size(max = 64, message = "Name length must be up to 64 characters.")
        String name,
        @NotBlank(message = "Quantity must be provided.")
        @Size(max = 16, message = "Quantity length must be up to 16 characters.")
        String quantity,
        @NotNull
        BuyingCategory category,
        @NotNull
        Boolean isPublic
) implements AssignableBuying {}