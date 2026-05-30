package com.example.backend.dtos.in.buyings;

import com.example.backend.validation.annotations.ValidPublicAssignment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@ValidPublicAssignment
public record CreateManyBuyingsDto(
        @NotNull
        @Size(min = 1, max = 50)
        List<CreateBuyingShortDto> buyings,
        @Min(1)
        Long assignedTo,
        @NotNull
        Boolean isPublic
) implements AssignableBuying {}
