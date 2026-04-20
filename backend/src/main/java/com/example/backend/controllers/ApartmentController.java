package com.example.backend.controllers;

import com.example.backend.dtos.in.apartment.CreateApartmentDto;
import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.ApartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {
    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @PostMapping
    public ResponseEntity<ProfileDto> create(
            @RequestBody CreateApartmentDto dto,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(apartmentService.create(user, dto));
    }
}
