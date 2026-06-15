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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventNotificationHandler eventNotificationHandler;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldCreate() {
        User user = mock(User.class);

        Profile profile = new Profile();
        when(user.getCurrentProfile()).thenReturn(profile);

        CreateEventRequest dto = new CreateEventRequest(
                "Собрание",
                LocalDate.of(2026, 1, 1),
                LocalTime.of(10, 0),
                "Важное собрание"
        );

        Long eventId = 1L;

        Event savedEvent = new Event();
        ReflectionTestUtils.setField(savedEvent, "id", eventId);

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        IdResponse<Long> response = eventService.create(user, dto);

        verify(eventRepository).save(any(Event.class));
        verify(eventNotificationHandler).handleEventNotification(user, savedEvent, true);

        assertEquals(eventId, response.id());
    }

    @Test
    void shouldGetEventDates() {
        Integer apartmentId = 1;
        Integer year = 2026;
        Month month = Month.JANUARY;

        LocalDate start = YearMonth.of(year, month).atDay(1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<LocalDate> expected = List.of(
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 1, 10)
        );

        when(eventRepository.findDistinctDatesByApartmentIdAndDateBetween(
                apartmentId, start, end
        )).thenReturn(expected);

        List<LocalDate> result = eventService.getEventDates(apartmentId, year, month);

        assertEquals(expected, result);

        verify(eventRepository).findDistinctDatesByApartmentIdAndDateBetween(
                apartmentId, start, end
        );
    }

    @Test
    void shouldGetEventsByDay() {
        Integer apartmentId = 1;
        LocalDate date = LocalDate.of(2026, 1, 1);

        Long eventId = 1L;

        Profile profile = new Profile();
        Event event = new Event();
        ReflectionTestUtils.setField(event, "id", eventId);
        ReflectionTestUtils.setField(event, "createdBy", profile);
        ReflectionTestUtils.setField(event, "date", date);

        when(eventRepository.findAllByCreatedBy_Apartment_IdAndDate(apartmentId, date))
                .thenReturn(List.of(event));

        List<EventDto> result = eventService.getEventsByDay(apartmentId, date);

        assertEquals(1, result.size());
        assertEquals(eventId, result.get(0).id());

        verify(eventRepository).findAllByCreatedBy_Apartment_IdAndDate(apartmentId, date);
    }

    @Test
    void shouldDeleteOneIfUserIsEventCreator() {
        User user = mock(User.class);

        Long eventId = 10L;

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(1L);

        Event event = new Event();
        ReflectionTestUtils.setField(event, "createdBy", creator);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.deleteOne(user, eventId);

        verify(eventRepository).findById(eventId);
        verify(eventRepository).deleteById(eventId);
        verify(eventNotificationHandler).handleEventNotification(user, event, false);
    }

    @Test
    void shouldThrowOnDeleteOneIfEventNotFound() {
        User user = mock(User.class);

        Long eventId = 10L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.deleteOne(user, eventId));

        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).deleteById(any());
        verifyNoInteractions(eventNotificationHandler);
    }

    @Test
    void shouldDeleteOneIfUserIsAdminAndIsNotEventCreator() {
        User user = mock(User.class);

        Long eventId = 10L;

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getRole()).thenReturn(Role.ADMIN);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(2L);

        Event event = new Event();
        ReflectionTestUtils.setField(event, "createdBy", creator);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.deleteOne(user, eventId);

        verify(eventRepository).deleteById(eventId);
        verify(eventNotificationHandler).handleEventNotification(user, event, false);
    }

    @Test
    void shouldDeleteOneIfUserIsApartmentCreatorIsNotEventCreator() {
        User user = mock(User.class);

        Long eventId = 10L;

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getRole()).thenReturn(Role.CREATOR);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(2L);

        Event event = new Event();
        ReflectionTestUtils.setField(event, "createdBy", creator);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.deleteOne(user, eventId);

        verify(eventRepository).deleteById(eventId);
        verify(eventNotificationHandler).handleEventNotification(user, event, false);
    }

    @Test
    void shouldThrowIfUserIsInhabitantAndNotEventCreator() {
        User user = mock(User.class);

        Long eventId = 10L;

        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(1L);
        when(profile.getRole()).thenReturn(Role.INHABITANT);
        when(user.getCurrentProfile()).thenReturn(profile);

        Profile creator = mock(Profile.class);
        when(creator.getId()).thenReturn(2L);

        Event event = new Event();
        ReflectionTestUtils.setField(event, "createdBy", creator);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(AccessForbiddenException.class,
                () -> eventService.deleteOne(user, eventId));

        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).deleteById(any());
        verifyNoInteractions(eventNotificationHandler);
    }
}
