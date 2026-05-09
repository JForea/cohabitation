package com.example.backend.repositories;

import com.example.backend.entities.DeviceToken;
import com.example.backend.entities.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface DeviceTokenRepository extends ListCrudRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByUserAndDeviceId(User user, String deviceId);
}
