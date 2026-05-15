package com.example.backend.controllers;

import com.example.backend.dtos.in.expenses.CreateExpenseRequest;
import com.example.backend.dtos.out.expenses.CreateExpenseResponse;
import com.example.backend.dtos.out.expenses.ExpenseDto;
import com.example.backend.entities.User;
import com.example.backend.intefaces.FileStorage;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.ExpenseService;
import com.example.backend.types.BuyingCategory;
import com.example.backend.types.ExpenseCategory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("@apartmentSecurity.hasAccess(#apartmentId, authentication)")
@RequestMapping("/api/apartments/{apartmentId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    private final FileStorage fileStorage;

    public ExpenseController(ExpenseService expenseService,
                             FileStorage fileStorage) {
        this.expenseService = expenseService;
        this.fileStorage = fileStorage;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateExpenseResponse> create(
            @PathVariable Integer apartmentId,
            @RequestPart("data") @Valid CreateExpenseRequest dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        String checkImageName = fileStorage.save("checks", image);
        CreateExpenseResponse response = expenseService.create(user, apartmentId, dto, checkImageName);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> get(
            @PathVariable Integer apartmentId,
            @RequestParam(defaultValue = "0") Short page,
            @RequestParam(defaultValue = "10") Short size
    ) {
        List<ExpenseDto> expenses = expenseService.get(apartmentId, page, size);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/amount")
    public ResponseEntity<Integer> getAmount(
            @PathVariable Integer apartmentId
    ) {
        return ResponseEntity.ok(expenseService.getAmount(apartmentId));
    }

    @GetMapping("/stats/categories")
    public ResponseEntity<Map<ExpenseCategory, Integer>> getSumByCategory(
            @PathVariable Integer apartmentId,
            @RequestParam YearMonth period
    ) {
        return ResponseEntity.ok(expenseService.getSumByCategory(apartmentId, period));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMany(
            @PathVariable Integer apartmentId,
            @RequestBody List<Long> ids,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        User user = details.getUser();
        expenseService.deleteMany(user, apartmentId, ids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
