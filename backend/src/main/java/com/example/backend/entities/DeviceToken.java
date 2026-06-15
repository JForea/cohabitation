package com.example.backend.entities;

import com.example.backend.types.Platform;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "device_token",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "device_id"})
        }
)
public class DeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "device_id", nullable = false)
    String deviceId;

    @Column(name = "token", nullable = false)
    String token;

    @Column(name = "platform", nullable = false)
    Platform platform;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    Instant createdAt;

    @Column(name = "updated_at")
    Instant updatedAt;

    public DeviceToken() {}

    public DeviceToken(
           User user,
           String deviceId,
           String token,
           Platform platform
    ) {
        this.user = user;
        this.deviceId = deviceId;
        this.token = token;
        this.platform = platform;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
