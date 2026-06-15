package com.example.backend.controllers;

import com.example.backend.annotations.WithMockCustomUser;
import com.example.backend.configurations.SecurityConfig;
import com.example.backend.dtos.in.user.AuthenticationDto;
import com.example.backend.dtos.in.user.LogoutRequest;
import com.example.backend.dtos.in.user.RegisterDto;
import com.example.backend.services.DeviceTokenService;
import com.example.backend.services.JwtService;
import com.example.backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, ObjectMapper.class})
public class UserControllerTest extends ControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private DeviceTokenService deviceTokenService;

    @Test
    void shouldAllowRegisterIfNotAuthenticated() throws Exception {
        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                "Марк",
                true,
                null
        );

        mockMvc.perform(post("/api/users/auth/registry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void shouldNotAllowRegisterIfAuthenticated() throws Exception {
        RegisterDto dto = new RegisterDto(
                "test@gmail.com",
                "password",
                "Марк",
                true,
                null
        );

        mockMvc.perform(post("/api/users/auth/registry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowLoginIfNotAuthenticated() throws Exception {
        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "password",
                null
        );

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void shouldNotAllowLoginIfAuthenticated() throws Exception {
        AuthenticationDto dto = new AuthenticationDto(
                "test@gmail.com",
                "password",
                null
        );

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotAllowLogoutIfNotAuthenticated() throws Exception {
        LogoutRequest dto = new LogoutRequest("deviceId");

        mockMvc.perform(post("/api/users/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowLogoutIfAuthenticated() throws Exception {
        LogoutRequest dto = new LogoutRequest("deviceId");

        mockMvc.perform(post("/api/users/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldNotAllowMeIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me")).andExpect(status().isForbidden());

    }

    @Test
    @WithMockCustomUser
    void shouldAllowMeIfAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me")).andExpect(status().isOk());
    }

}
