package com.example.backend.services;

import com.example.backend.dtos.in.events.CreateEventRequest;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.events.EventDto;
import com.example.backend.entities.Event;
import com.example.backend.entities.User;
import com.example.backend.intefaces.EventNotificationHandler;
import com.example.backend.repositories.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;

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

        eventNotificationHandler.handleEventNotification(user, apartmentId, event, true);

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
}
