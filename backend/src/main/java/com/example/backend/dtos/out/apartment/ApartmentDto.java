package com.example.backend.dtos.out.apartment;

public record ApartmentDto(
        Integer id,
        String name,
        String address,
        Integer budget,
        Integer currentExpenseSum
) {

}
