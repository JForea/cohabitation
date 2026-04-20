package com.example.backend.repositories;

import com.example.backend.entities.Event;
import org.springframework.data.repository.ListCrudRepository;

public interface EventRepository extends ListCrudRepository<Event, Long> {
}
