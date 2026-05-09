package com.example.backend.repositories;

import com.example.backend.entities.Profile;
import com.example.backend.entities.ProfileNotification;
import com.example.backend.entities.keys.ProfileNotificationKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileNotificationRepository extends ListCrudRepository<ProfileNotification, ProfileNotificationKey> {
    Page<ProfileNotification> findAllByKey_Profile(Profile profile, Pageable pageable);
    Optional<ProfileNotification> findByKey_ProfileAndKey_Notification_Id(Profile profile, Long notificationId);
    List<ProfileNotification> findAllByKey_ProfileAndIsReadFalse(Profile profile);
    @Query("""
        SELECT count(p)
        FROM ProfileNotification p
        WHERE key.profile = :profile
            AND isRead = false
    """)
    Integer getCountUnreadByProfile(Profile profile);
}
