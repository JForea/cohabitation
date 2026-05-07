package com.example.backend.services;

import com.example.backend.dtos.in.events.CreateEventRequest;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.entities.Event;
import com.example.backend.entities.User;
import com.example.backend.repositories.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public IdResponse<Long> create(User user, CreateEventRequest dto) {
        Event event = eventRepository.save(new Event(
                user.getCurrentProfile(),
                dto.name(),
                dto.date(),
                dto.time(),
                dto.description()
        ));

        return new IdResponse<>(event.getId());
    }
}
