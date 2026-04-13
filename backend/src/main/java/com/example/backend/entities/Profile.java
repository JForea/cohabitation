package com.example.backend.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 63)
    private String name;

    @Column(name = "points", nullable = false)
    private Integer points = 0;

    @CreationTimestamp
    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @OneToMany(mappedBy = "createdBy", orphanRemoval = true)
    private Set<Expense> expenses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdBy", orphanRemoval = true)
    private Set<Buying> createdByings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdBy", orphanRemoval = true)
    private Set<Buying> assignedBuyings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "completedBy", orphanRemoval = true)
    private Set<Buying> completedBuyings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdBy", orphanRemoval = true)
    private Set<Task> createdTasks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdBy", orphanRemoval = true)
    private Set<Task> assignedTasks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "completedBy", orphanRemoval = true)
    private Set<Task> completedTasks = new LinkedHashSet<>();

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public Set<Task> getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Set<Task> completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Set<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(Set<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public Set<Task> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(Set<Task> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public Set<Buying> getCompletedBuyings() {
        return completedBuyings;
    }

    public void setCompletedBuyings(Set<Buying> completedBuyings) {
        this.completedBuyings = completedBuyings;
    }

    public Set<Buying> getAssignedBuyings() {
        return assignedBuyings;
    }

    public void setAssignedBuyings(Set<Buying> assignedBuyings) {
        this.assignedBuyings = assignedBuyings;
    }

    public Set<Buying> getCreatedByings() {
        return createdByings;
    }

    public void setCreatedByings(Set<Buying> createdByings) {
        this.createdByings = createdByings;
    }

    public Set<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Instant getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(Instant leftAt) {
        this.leftAt = leftAt;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}