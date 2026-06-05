package com.example.backend.dtos.in.buyings;

import com.example.backend.types.BuyingCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBuyingShortDto(
        @NotBlank(message = "Name must be provided.")
        @Size(max = 64, message = "Name length must be up to 64 characters.")
        String name,
        @NotBlank(message = "Quantity must be provided.")
        @Size(max = 16, message = "Quantity length must be up to 16 characters.")
        String quantity,
        @NotNull
        BuyingCategory category
) {}
