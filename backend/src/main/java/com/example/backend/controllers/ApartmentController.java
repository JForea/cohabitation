package com.example.backend.controllers;

import com.example.backend.dtos.in.apartment.CreateApartmentDto;
import com.example.backend.dtos.out.apartment.ApartmentDto;
import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.ApartmentService;
import com.example.backend.services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {
    private final ApartmentService apartmentService;

    private final JwtService jwtService;

    public ApartmentController(ApartmentService apartmentService,
                               JwtService jwtService) {
        this.apartmentService = apartmentService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<ProfileDto> create(
            @RequestBody @Valid CreateApartmentDto dto,
            @AuthenticationPrincipal CustomUserDetails details,
            HttpServletResponse servletResponse
    ) {
        User user = details.getUser();
        ProfileDto profileDto = apartmentService.create(user, dto);
        String token = jwtService.generateToken(user, profileDto);
        servletResponse.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileDto);
    }

    @GetMapping("/{apartmentId}")
    public ResponseEntity<ApartmentDto> get(
            @PathVariable Integer apartmentId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        ApartmentDto dto = apartmentService.get(user, apartmentId);
        return ResponseEntity.ok(dto);
    }
}
