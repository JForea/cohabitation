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

    @Column(name = "sum", nullable = false)
    private Integer sum;

    @Enumerated
    @Column(name = "category", nullable = false)
    private ExpenseCategory category;

    @Column(name = "check_image_name", length = 37)
    private String checkImageName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile createdBy;

    public Expense() {}

    public Expense(
            String name,
            Integer sum,
            ExpenseCategory category,
            String checkImageName,
            Profile createdBy
    ) {
        this.name = name;
        this.sum = sum;
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
        return sum;
    }

    public void setSum(Integer sum) {this.sum = sum;}

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

}