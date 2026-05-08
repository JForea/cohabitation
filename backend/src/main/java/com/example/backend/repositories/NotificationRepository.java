package com.example.backend.repositories;

import com.example.backend.entities.Notification;
import org.springframework.data.repository.ListCrudRepository;


public interface NotificationRepository extends ListCrudRepository<Notification, Long> {
}
