package com.example.backend.controllers;

import com.example.backend.annotations.WithMockCustomUser;
import com.example.backend.configurations.SecurityConfig;
import com.example.backend.entities.User;
import com.example.backend.services.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@Import({SecurityConfig.class, ObjectMapper.class})
public class NotificationControllerTest extends ControllerTest {

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void shouldNotAllowGetUnreadCountIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/notifications/unread-count"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetUnreadCountIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/notifications/unread-count"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetUnreadCountIfInApartment() throws Exception {
        when(notificationService.getUnreadCount(any(User.class))).thenReturn(1);

        mockMvc.perform(get("/api/apartments/1/notifications/unread-count"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowGetNotificationsIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/notifications"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetNotificationsIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/notifications"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetNotificationsIfInApartment() throws Exception {
        when(notificationService.findPersonal(any(User.class), anyShort(), anyShort()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/apartments/1/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowReadAllNotificationsIfNotAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/notifications"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowReadAllNotificationsIfNotInApartment() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/notifications"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowReadAllNotificationsIfInApartment() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/notifications"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowReadNotificationIfNotAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/notifications/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowReadNotificationIfNotInApartment() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/notifications/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowReadNotificationIfInApartment() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/notifications/1"))
                .andExpect(status().isNoContent());
    }

}