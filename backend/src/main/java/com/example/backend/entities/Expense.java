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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name="name", nullable = false, length = 64)
    private String name;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Enumerated
    @Column(name = "category", nullable = false)
    private ExpenseCategory category;

    @Column(name = "check_image_name", length = 64)
    private String checkImageName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    public Expense() {}

    public Expense(
            Apartment apartment,
            String name,
            Integer amount,
            ExpenseCategory category,
            String checkImageName,
            Profile createdBy
    ) {
        this.apartment = apartment;
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.checkImageName = checkImageName;
        this.createdBy = createdBy;
    }

    public Profile getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Profile createdBy) {
        this.createdBy = createdBy;
    }

    public String getCheckImageName() {
        return checkImageName;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {this.name = name;}

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {this.amount = amount;}

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    public Apartment getApartment() {
        return apartment;
    }
}