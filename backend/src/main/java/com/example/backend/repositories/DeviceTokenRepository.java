package com.example.backend.repositories;

import com.example.backend.entities.DeviceToken;
import org.springframework.data.repository.ListCrudRepository;

public interface DeviceTokenRepository extends ListCrudRepository<DeviceToken, Long> {
}
