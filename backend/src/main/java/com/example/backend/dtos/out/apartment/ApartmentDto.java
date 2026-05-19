package com.example.backend.dtos.out.apartment;

import com.example.backend.entities.Apartment;

import java.time.LocalDate;
import java.time.ZoneId;

public record ApartmentDto(
        Integer id,
        String name,
        String address,
        Integer budget,
        Integer currentExpenseSum,
        String inviteCode,
        LocalDate createdAt) {
    public ApartmentDto(Apartment apartment, Integer currentExpenseSum, String inviteCode) {
        this(
                apartment.getId(),
                apartment.getName(),
                apartment.getAddress(),
                apartment.getBudget(),
                currentExpenseSum,
                inviteCode,
                apartment.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate()
        );
    }
}
