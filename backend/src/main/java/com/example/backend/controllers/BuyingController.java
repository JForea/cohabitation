package com.example.backend.controllers;

import com.example.backend.dtos.in.buyings.CreateBuyingDto;
import com.example.backend.dtos.in.buyings.CreateManyBuyingsDto;
import com.example.backend.dtos.out.buyings.BuyingDto;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.common.StatusResponse;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.BuyingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("@apartmentSecurity.hasAccess(#apartmentId, authentication)")
@RequestMapping("/api/apartments/{apartmentId}/buyings")
public class BuyingController {

    private final BuyingService buyingService;

    public BuyingController(BuyingService buyingService) {
        this.buyingService = buyingService;
    }

    @PostMapping
    public ResponseEntity<IdResponse<Long>> create(
            @RequestBody @Valid CreateBuyingDto dto,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                buyingService.create(user, dto)
        );
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<IdResponse<Long>>> createMany(
            @RequestBody @Valid CreateManyBuyingsDto dto,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                buyingService.createMany(user, dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<BuyingDto>> get(
            @PathVariable Integer apartmentId,
            @RequestParam(required = false) Integer assignedTo,
            @RequestParam(required = false) Boolean isPublic,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.ok(
            buyingService.get(apartmentId, user, assignedTo, isPublic)
        );
    }

    @PatchMapping("/{buyingId}")
    public ResponseEntity<StatusResponse> changeStatus(
            @PathVariable Integer apartmentId,
            @PathVariable Long buyingId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.ok(
                buyingService.changeStatus(apartmentId, user, buyingId)
        );
    }

    @DeleteMapping("/{buyingId}")
    public ResponseEntity<Void> deleteOne(
            @PathVariable Integer apartmentId,
            @PathVariable Long buyingId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        buyingService.deleteOne(apartmentId, user, buyingId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMany(
            @PathVariable Integer apartmentId,
            @RequestBody List<Long> ids,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        buyingService.deleteMany(user, apartmentId, ids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
