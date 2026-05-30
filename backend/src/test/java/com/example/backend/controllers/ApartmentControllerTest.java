package com.example.backend.controllers;

import com.example.backend.annotations.WithMockCustomUser;
import com.example.backend.configurations.SecurityConfig;
import com.example.backend.dtos.in.apartment.CreateApartmentDto;
import com.example.backend.dtos.in.apartment.SetBudgetRequest;
import com.example.backend.dtos.out.apartment.CreateApartmentResponse;
import com.example.backend.dtos.out.apartment.JoinApartmentResponse;
import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.entities.User;
import com.example.backend.services.ApartmentService;
import com.example.backend.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApartmentController.class)
@Import({SecurityConfig.class, ObjectMapper.class})
public class ApartmentControllerTest extends ControllerTest {

    @MockitoBean
    private ApartmentService apartmentService;

    @Test
    void shouldNotAllowCreateApartmentIfNotAuthenticated() throws Exception {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "ул. Ленина, 42",
                (short) 0
        );

        mockMvc.perform(post("/api/apartments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldNotAllowCreateApartmentIfInApartment() throws Exception {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "ул. Ленина, 42",
                (short) 0
        );

        mockMvc.perform(post("/api/apartments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldAllowCreateApartmentIfNotInApartment() throws Exception {
        CreateApartmentDto dto = new CreateApartmentDto(
                "Квартира",
                "ул. Ленина, 42",
                (short) 0
        );

        CreateApartmentResponse response = mock(CreateApartmentResponse.class);
        ProfileDto profile = mock(ProfileDto.class);

        when(response.profile()).thenReturn(profile);

        when(apartmentService.create(any(User.class), any(CreateApartmentDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/apartments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotAllowJoinApartmentIfNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/apartments/join")
                        .param("code", "HOME0001"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldNotAllowJoinApartmentIfInApartment() throws Exception {
        mockMvc.perform(post("/api/apartments/join")
                        .param("code", "HOME0001"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldAllowJoinApartmentIfNotInApartment() throws Exception {
        JoinApartmentResponse response = mock(JoinApartmentResponse.class);
        ProfileDto profile = mock(ProfileDto.class);

        when(response.profile()).thenReturn(profile);

        when(apartmentService.join(any(User.class), anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/api/apartments/join")
                        .param("code", "HOME0001"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowLeaveApartmentIfNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/apartments/leave")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "CREATOR")
    void shouldNotAllowLeaveApartmentIfNotInApartment() throws Exception {
        mockMvc.perform(post("/api/apartments/leave")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "CREATOR")
    void shouldNotAllowLeaveApartmentIfCreator() throws Exception {
        mockMvc.perform(post("/api/apartments/leave")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowLeaveApartmentIfNotCreator() throws Exception {
        mockMvc.perform(post("/api/apartments/leave")).andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowGetApartmentIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetApartmentIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetApartmentIfInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1")).andExpect(status().isOk());
    }

    @Test
    void shouldNotGenerateCodeIfNotAuthorized() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/code")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotGenerateCodeIfNotInApartment() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/code")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldNotGenerateCodeIfInhabitant() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/code")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "ADMIN")
    void shouldGenerateCodeIfAdmin() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/code")).andExpect(status().isOk());
    }

    @Test
    void shouldNotPatchBudgetIfNotAuthorized() throws Exception {
        SetBudgetRequest dto = new SetBudgetRequest(50000);

        mockMvc.perform(patch("/api/apartments/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotPatchBudgetIfNotInApartment() throws Exception {
        SetBudgetRequest dto = new SetBudgetRequest(50000);

        mockMvc.perform(patch("/api/apartments/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldNotPatchBudgetIfInhabitant() throws Exception {
        SetBudgetRequest dto = new SetBudgetRequest(50000);

        mockMvc.perform(patch("/api/apartments/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "ADMIN")
    void shouldPatchBudgetIfAdmin() throws Exception {
        SetBudgetRequest dto = new SetBudgetRequest(50000);

        mockMvc.perform(patch("/api/apartments/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteApartmentIfNotAuthorized() throws Exception {
        mockMvc.perform(delete("/api/apartments/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotDeleteApartmentIfNotInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "ADMIN")
    void shouldNotDeleteApartmentIfAdmin() throws Exception {
        mockMvc.perform(delete("/api/apartments/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "CREATOR")
    void shouldDeleteApartmentIfCreator() throws Exception {
        mockMvc.perform(delete("/api/apartments/1")).andExpect(status().isNoContent());
    }

}
