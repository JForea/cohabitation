package com.example.backend.entities;

import com.example.backend.types.ExpenseCategory;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "expense")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Enumerated
    @Column(name = "category", nullable = false)
    private ExpenseCategory category;

    @Column(name = "check_image_ref", length = 32)
    private String checkImageRef;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile createdBy;

    public Profile getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Profile createdBy) {
        this.createdBy = createdBy;
    }

    public String getCheckImageRef() {
        return checkImageRef;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public Integer getPrice() {
        return price;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public Long getId() {
        return id;
    }

}