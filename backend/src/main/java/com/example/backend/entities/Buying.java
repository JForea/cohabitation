package com.example.backend.entities;

import com.example.backend.dtos.in.buyings.CreateBuyingDto;
import com.example.backend.types.BuyingCategory;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "buying")
public class Buying {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private Profile createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private Profile assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by")
    private Profile completedBy;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "quantity", nullable = false, length = 16)
    private String quantity;

    @Enumerated
    @Column(name = "category", nullable = false)
    private BuyingCategory  category = BuyingCategory.OTHER;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public Buying() {}

    public Buying(
            Profile createdBy,
            Profile assignedTo,
            Apartment apartment,
            String name,
            String quantity,
            BuyingCategory category,
            Boolean isPublic
    ) {
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.apartment = apartment;
        this.name = name;
        this.quantity = quantity;
        if (category != null)
            this.category = category;
        this.isPublic = isPublic;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public BuyingCategory getCategory() {
        return category;
    }

    public void setCategory(BuyingCategory category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Profile getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(Profile completedBy) {
        this.completedBy = completedBy;
    }

    public Profile getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Profile assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Profile getCreatedBy() {
        return createdBy;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public Long getId() {
        return id;
    }


}