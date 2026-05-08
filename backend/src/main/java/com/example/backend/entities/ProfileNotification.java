package com.example.backend.entities;

import com.example.backend.entities.keys.ProfileNotificationKey;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_notification")
public class ProfileNotification {
    @EmbeddedId
    private ProfileNotificationKey key;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    public  ProfileNotification() {}

    public ProfileNotification(
            Profile profile,
            Notification notification
    ) {
        key = new ProfileNotificationKey(profile, notification);
    }

    public ProfileNotificationKey getKey() {
        return key;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
