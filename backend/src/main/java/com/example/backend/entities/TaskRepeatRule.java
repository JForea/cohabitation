package com.example.backend.entities;

import com.example.backend.types.Room;
import com.example.backend.types.TaskPriority;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "task_repeat_rule",
        check = {
                @CheckConstraint(name = "CK_task_repeat_rule_points", constraint = "points >= 0"),
                @CheckConstraint(name = "CK_task_repeat_rule_interval", constraint = "interval_days > 0")
        }
)
public class TaskRepeatRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private Profile createdBy;

    @ManyToMany
    @JoinTable(
            name = "task_repeat_rule_assigned_profile",
            joinColumns = @JoinColumn(name = "repeat_rule_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private Set<Profile> assignedProfiles = new HashSet<>();

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
    private Short points = 0;

    @Column(name = "interval_days", nullable = false)
    private Short intervalDays;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public TaskRepeatRule() {}

    public TaskRepeatRule(
            Profile createdBy,
            List<Profile> assigned,
            String name,
            String description,
            Room room,
            TaskPriority priority,
            Short points,
            Short intervalDays,
            LocalDate startDate,
            LocalDate endDate
    ) {
        this.createdBy = createdBy;
        this.assignedProfiles = new HashSet<>(assigned);

        this.name = name;
        this.description = description;

        if (room != null)
            this.room = room;
        if (priority != null)
            this.priority = priority;
        if (points != null)
            this.points = points;

        this.intervalDays = intervalDays;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public Profile getCreatedBy() {
        return createdBy;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Room getRoom() {
        return room;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public Short getPoints() {
        return points;
    }

    public Short getIntervalDays() {
        return intervalDays;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Boolean getActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Profile> getAssignedProfiles() {
        return assignedProfiles;
    }
}