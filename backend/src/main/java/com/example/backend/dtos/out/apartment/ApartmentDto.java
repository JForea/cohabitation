package com.example.backend.dtos.out.apartment;

import com.example.backend.entities.Apartment;

public record ApartmentDto(
        Integer id,
        String name,
        String address,
        Integer budget,
        Integer currentExpenseSum
) {
    public ApartmentDto(Apartment apartment, Integer currentExpenseSum) {
        this(
                apartment.getId(),
                apartment.getName(),
                apartment.getAddress(),
                apartment.getBudget(),
                currentExpenseSum
        );
    }
}
