package com.example.backend.controllers;

import com.example.backend.dtos.in.buyings.CreateBuyingDto;
import com.example.backend.dtos.in.buyings.CreateManyBuyingsDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.BuyingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{apartmentId}/buyings")
public class BuyingController {

    private final BuyingService buyingService;

    public BuyingController(BuyingService buyingService) {
        this.buyingService = buyingService;
    }

    @PostMapping
    public ResponseEntity<Long> create(
            @PathVariable Integer apartmentId,
            @RequestBody CreateBuyingDto dto,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                buyingService.create(apartmentId, user, dto)
        );
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Long>> createMany(
            @PathVariable Integer apartmentId,
            @RequestBody CreateManyBuyingsDto dto,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                buyingService.createMany(apartmentId, user, dto)
        );
    }
}
