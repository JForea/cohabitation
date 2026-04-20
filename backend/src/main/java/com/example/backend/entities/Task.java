package com.example.backend.entities;

import com.example.backend.dtos.in.tasks.CreateTaskDto;
import com.example.backend.types.Room;
import com.example.backend.types.TaskPriority;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "task")
public class Task {
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
    @JoinColumn(name = "assigned_to_id")
    private Profile assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by")
    private Profile completedBy;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "description", length = 256)
    private String description;

    @Enumerated
    @Column(name = "room", nullable = false)
    private Room room = Room.COMMON;

    @Enumerated
    @Column(name = "priority", nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(name = "points", nullable = false)
    private Short points = 5;

    @Column(name = "repeat_time")
    private Short repeatTime;

    @Column(name = "due_date")
    private LocalDate dueTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public Task() {}

    public Task(
            Apartment apartment,
            Profile createdBy,
            Profile assignedTo,
            String name,
            String description,
            Room room,
            TaskPriority priority,
            Short points,
            Short repeatTime,
            LocalDate dueTime
    ) {
        this.apartment = apartment;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.name = name;
        this.description = description;
        if (room != null)
            this.room = room;
        if (priority != null)
            this.priority = priority;
        if (points != null)
            this.points = points;
        this.repeatTime = repeatTime;
        this.dueTime = dueTime;
    }

    public LocalDate getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalDate dueTime) {
        this.dueTime = dueTime;
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

    public Short getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(Short repeatTime) {
        this.repeatTime = repeatTime;
    }

    public Short getPoints() {
        return points;
    }

    public void setPoints(Short points) {
        this.points = points;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}