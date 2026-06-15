package com.example.backend.controllers;

import com.example.backend.annotations.WithMockCustomUser;
import com.example.backend.configurations.SecurityConfig;
import com.example.backend.entities.User;
import com.example.backend.services.ProfileService;
import com.example.backend.types.Role;
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

@WebMvcTest(ProfilesController.class)
@Import({SecurityConfig.class, ObjectMapper.class})
public class ProfileControllerTest extends ControllerTest {

    @MockitoBean
    private ProfileService profileService;

    @Test
    void shouldNotAllowGetProfilesIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/profiles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetProfilesIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/profiles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetProfilesIfInApartment() throws Exception {
        when(profileService.getAll(any(User.class), anyInt(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/apartments/1/profiles"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowKickProfileIfNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/apartments/1/profiles/1/kick"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowKickProfileIfNotInApartment() throws Exception {
        mockMvc.perform(post("/api/apartments/1/profiles/1/kick"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldNotAllowKickProfileIfInhabitant() throws Exception {
        mockMvc.perform(post("/api/apartments/1/profiles/1/kick"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "ADMIN")
    void shouldAllowKickProfileIfAdmin() throws Exception {
        mockMvc.perform(post("/api/apartments/1/profiles/1/kick"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowSetRoleIfNotAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/profiles/1")
                        .param("role", "ADMIN"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowSetRoleIfNotInApartment() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/profiles/1")
                        .param("role", "ADMIN"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "ADMIN")
    void shouldNotAllowSetRoleIfAdmin() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/profiles/1")
                        .param("role", "ADMIN"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "CREATOR")
    void shouldAllowSetRoleIfCreator() throws Exception {
        when(profileService.setRole(any(User.class), anyLong(), any(Role.class)))
                .thenReturn(Role.ADMIN);

        mockMvc.perform(patch("/api/apartments/1/profiles/1")
                        .param("role", "ADMIN"))
                .andExpect(status().isOk());
    }

}