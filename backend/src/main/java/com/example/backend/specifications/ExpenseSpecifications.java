package com.example.backend.specifications;

import com.example.backend.entities.Expense;
import com.example.backend.types.ExpenseCategory;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;

public class ExpenseSpecifications {
    public static Specification<Expense> byApartmentId(Integer apartmentId) {
        return (root, _, cb) ->
                cb.equal(root.get("createdBy").get("apartment").get("id"), apartmentId);
    }

    public static Specification<Expense> byProfileId(Long profileId) {
        return (root, _, cb) ->
                profileId == null ? cb.conjunction() : cb.equal(root.get("createdBy").get("id"), profileId);
    }

    public static Specification<Expense> byCategory(ExpenseCategory category) {
        return (root, _, cb) ->
                category == null ? cb.conjunction() : cb.equal(root.get("expenseCategory"), category);
    }

    public static Specification<Expense> byPeriod(YearMonth period) {
        return (root, _, cb) -> {
            Instant start = period
                    .atDay(1)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();

            Instant end = period
                    .plusMonths(1)
                    .atDay(1)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();

            return cb.and(
                    cb.greaterThanOrEqualTo(root.get("createdAt"), start),
                    cb.lessThan(root.get("createdAt"), end)
            );
        };
    }
}
