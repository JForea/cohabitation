package com.example.backend.repositories;

import com.example.backend.entities.Profile;
import com.example.backend.entities.ProfileNotification;
import com.example.backend.entities.keys.ProfileNotificationKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

public interface ProfileNotificationRepository extends ListCrudRepository<ProfileNotification, ProfileNotificationKey> {
    Page<ProfileNotification> findAllByKey_Profile(Profile profile, Pageable pageable);
}
