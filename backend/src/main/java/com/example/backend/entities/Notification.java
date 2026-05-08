package com.example.backend.entities;

import com.example.backend.types.EntityType;
import com.example.backend.types.NotificationType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private Profile actor;

    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @OneToMany(mappedBy = "key.notification", orphanRemoval = true)
    private Set<ProfileNotification> profileNotifications = new LinkedHashSet<>();

    public Notification() {}

    public Notification(
            Profile actor,
            NotificationType type,
            EntityType entityType,
            Map<String, Object> payload
    ) {
        this.actor = actor;
        this.type = type;
        this.entityType = entityType;
        this.payload = payload;
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Profile getActor() {
        return actor;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public NotificationType getType() {
        return type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public Set<ProfileNotification> getProfileNotifications() {
        return profileNotifications;
    }
}
