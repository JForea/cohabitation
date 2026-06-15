package com.example.backend.specifications;

import com.example.backend.entities.Buying;
import org.springframework.data.jpa.domain.Specification;

public class BuyingSpecifications {
    public static Specification<Buying> byApartment(Integer id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            return cb.equal(root.get("createdBy").get("apartment").get("id"), id);
        };
    }

    public static Specification<Buying> byAssignedTo(Integer id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            return cb.and(
                    cb.equal(root.get("assignedTo").get("id"), id)
            );
        };
    }

    public static Specification<Buying> byPublic(Boolean isPublic, Integer userId) {
        return (root, query, cb) -> {
            if (isPublic == null || isPublic)
                return cb.isTrue(root.get("isPublic"));
            return cb.and(
                    cb.equal(root.get("createdBy").get("id"), userId),
                    cb.isFalse(cb.isTrue(root.get("isPublic")))
            );
        };
    }
}
