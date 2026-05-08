package com.example.backend.repositories;

import com.example.backend.entities.ProfileNotification;
import com.example.backend.entities.keys.ProfileNotificationKey;
import org.springframework.data.repository.ListCrudRepository;

public interface ProfileNotificationRepository extends ListCrudRepository<ProfileNotification, ProfileNotificationKey> {
}
