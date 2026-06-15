package com.example.backend.entities.keys;

import com.example.backend.entities.Notification;
import com.example.backend.entities.Profile;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Embeddable
public class ProfileNotificationKey {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    public ProfileNotificationKey() {}

    public ProfileNotificationKey(
            Profile profile,
            Notification notification
    ) {
        this.profile = profile;
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProfileNotificationKey that = (ProfileNotificationKey) o;

        return Objects.equals(
                profile != null ? profile.getId() : null,
                that.profile != null ? that.profile.getId() : null
        ) &&
                Objects.equals(
                        notification != null ? notification.getId() : null,
                        that.notification != null ? that.notification.getId() : null
                );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                profile != null ? profile.getId() : null,
                notification != null ? notification.getId() : null
        );
    }
}
