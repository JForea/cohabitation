package com.example.backend.controllers;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.ProfileService;
import com.example.backend.types.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("@apartmentSecurity.hasAccess(#apartmentId, authentication)")
@RequestMapping("/api/apartments/{apartmentId}/profiles")
public class ProfilesController {

    private final ProfileService profileService;

    public ProfilesController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<List<ProfileDto>> getAll(
            @PathVariable Integer apartmentId,
            @RequestParam(required = false) Boolean excludeMe,
            @AuthenticationPrincipal CustomUserDetails details
            ) {
        User user = details.getUser();
        List<ProfileDto> profiles = profileService.getAll(user, apartmentId, excludeMe);
        return ResponseEntity.ok(profiles);
    }

    @PostMapping("/{profileId}/kick")
    public ResponseEntity<Void> kick(
            @PathVariable Long profileId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        profileService.kick(user, profileId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{profileId}")
    public ResponseEntity<Role> setRole(
            @PathVariable Long profileId,
            @RequestParam Role role,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        Role newRole = profileService.setRole(user, profileId, role);
        if (newRole == null)
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(newRole);
    }
}
