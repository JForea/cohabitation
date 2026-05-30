package com.example.backend.controllers;

import com.example.backend.annotations.WithMockCustomUser;
import com.example.backend.configurations.SecurityConfig;
import com.example.backend.dtos.in.tasks.CreateTaskDto;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.common.StatusResponse;
import com.example.backend.entities.User;
import com.example.backend.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import({SecurityConfig.class, ObjectMapper.class})
public class TaskControllerTest extends ControllerTest {

    @MockitoBean
    private TaskService taskService;

    @Test
    void shouldNotAllowCreateTaskIfNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/apartments/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Уборка"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowCreateTaskIfNotInApartment() throws Exception {
        mockMvc.perform(post("/api/apartments/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Уборка"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowCreateTaskIfInApartment() throws Exception {
        when(taskService.create(any(User.class), any(CreateTaskDto.class)))
                .thenReturn(new IdResponse<>(1L));

        mockMvc.perform(post("/api/apartments/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Уборка"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotAllowGetTasksIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/tasks"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetTasksIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/tasks"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetTasksIfInApartment() throws Exception {
        when(taskService.getTasks(anyInt(), anyShort(), anyShort(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/apartments/1/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowChangeTaskStatusIfNotAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/tasks/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowChangeTaskStatusIfNotInApartment() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/tasks/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowChangeTaskStatusIfInApartment() throws Exception {
        StatusResponse response = mock(StatusResponse.class);

        when(taskService.switchTaskStatus(any(User.class), anyLong()))
                .thenReturn(response);

        mockMvc.perform(patch("/api/apartments/1/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowDeleteTaskIfNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/tasks/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowDeleteTaskIfNotInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/tasks/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowDeleteTaskIfInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowDeleteManyTasksIfNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowDeleteManyTasksIfNotInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowDeleteManyTasksIfInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isNoContent());
    }

}