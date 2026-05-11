package com.example.backend.services;

import com.example.backend.dtos.in.events.CreateEventRequest;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.events.EventDto;
import com.example.backend.entities.Event;
import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.intefaces.EventNotificationHandler;
import com.example.backend.repositories.EventRepository;
import com.example.backend.types.Role;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@Service
public class EventService {

    private final EventRepository eventRepository;

    private final EventNotificationHandler eventNotificationHandler;

    public EventService(EventRepository eventRepository,
                        EventNotificationHandler eventNotificationHandler) {
        this.eventRepository = eventRepository;
        this.eventNotificationHandler = eventNotificationHandler;
    }

    @Transactional
    public IdResponse<Long> create(User user, Integer apartmentId, CreateEventRequest dto) {
        Event event = eventRepository.save(new Event(
                user.getCurrentProfile(),
                dto.name(),
                dto.date(),
                dto.time(),
                dto.description()
        ));

        eventNotificationHandler.handleEventNotification(user, event, true);

        return new IdResponse<>(event.getId());
    }

    public List<LocalDate> getEventDates(Integer apartmentId, Integer year, Month month) {
        LocalDate start = YearMonth.of(year, month).atDay(1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return eventRepository.findDistinctDatesByApartmentIdAndDateBetween(apartmentId, start, end);
    }

    public List<EventDto> getEventsByDay(Integer apartmentId, LocalDate date) {
        return eventRepository.findAllByCreatedBy_Apartment_IdAndDate(apartmentId, date)
                .stream().map(EventDto::new).toList();
    }

    public void deleteOne(User user, Long eventId) {
        Profile profile = user.getCurrentProfile();
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Event not found.")
        );

        if (!Objects.equals(event.getCreatedBy().getId(), profile.getId()) && profile.getRole() == Role.INHABITANT)
            throw new AccessForbiddenException("Can't delete others events");

        eventRepository.deleteById(eventId);

        eventNotificationHandler.handleEventNotification(user, event, false);
    }
}
