package com.example.backend.dtos.in.buyings;

import com.example.backend.types.BuyingCategory;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateBuyingShortDto(
        @NotBlank(message = "Name must be provided.")
        @Length(max = 64, message = "Name length must be up to 64 characters.")
        String name,
        @NotBlank(message = "Quantity must be provided.")
        @Length(max = 16, message = "Quantity length must be up to 16 characters.")
        String quantity,
        BuyingCategory category
) {}
