package com.example.backend.controllers;

import com.example.backend.annotations.WithMockCustomUser;
import com.example.backend.configurations.SecurityConfig;
import com.example.backend.dtos.in.events.CreateEventRequest;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.entities.User;
import com.example.backend.services.EventService;
import com.example.backend.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@Import({SecurityConfig.class, ObjectMapper.class})
public class EventControllerTest extends ControllerTest {

    @MockitoBean
    private EventService eventService;

    @Test
    void shouldNotAllowCreateEventIfNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/apartments/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Уборка",
                                  "date": "2026-06-01"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowCreateEventIfNotInApartment() throws Exception {
        mockMvc.perform(post("/api/apartments/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Уборка",
                                  "date": "2026-06-01"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowCreateEventIfInApartment() throws Exception {
        when(eventService.create(any(User.class), any(CreateEventRequest.class)))
                .thenReturn(new IdResponse<>(1L));

        mockMvc.perform(post("/api/apartments/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Уборка",
                                  "date": "2026-06-01"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotAllowGetCalendarIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/events/calendar")
                        .param("year", "2026")
                        .param("month", "JUNE"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetCalendarIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/events/calendar")
                        .param("year", "2026")
                        .param("month", "JUNE"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetCalendarIfInApartment() throws Exception {
        when(eventService.getEventDates(anyInt(), anyInt(), any(Month.class)))
                .thenReturn(List.of(LocalDate.of(2026, 6, 1)));

        mockMvc.perform(get("/api/apartments/1/events/calendar")
                        .param("year", "2026")
                        .param("month", "JUNE"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowGetEventsByDayIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/events/day/2026-06-01"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetEventsByDayIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/events/day/2026-06-01"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetEventsByDayIfInApartment() throws Exception {
        when(eventService.getEventsByDay(anyInt(), any(LocalDate.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/apartments/1/events/day/2026-06-01"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowDeleteEventIfNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/events/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowDeleteEventIfNotInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/events/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowDeleteEventIfInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/events/1"))
                .andExpect(status().isNoContent());
    }

}