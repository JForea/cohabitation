package com.example.backend.controllers;

import com.example.backend.dtos.in.rules.CreateRuleRequest;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.rules.RuleDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.RuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("@apartmentSecurity.hasAccess(#apartmentId, authentication)")
@RequestMapping("/api/apartments/{apartmentId}/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping
    public ResponseEntity<IdResponse<Long>> create(
            @PathVariable Integer apartmentId,
            @RequestBody @Valid CreateRuleRequest dto,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(ruleService.create(user, apartmentId, dto));
    }

    @GetMapping
    public ResponseEntity<List<RuleDto>> get(
            @PathVariable Integer apartmentId
    ) {
        return ResponseEntity.ok(ruleService.get(apartmentId));
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long ruleId,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        ruleService.deleteOne(user, ruleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
