package com.example.backend.controllers;

import com.example.backend.annotations.WithMockCustomUser;
import com.example.backend.configurations.SecurityConfig;
import com.example.backend.dtos.in.rules.CreateRuleRequest;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.entities.User;
import com.example.backend.services.RuleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RuleController.class)
@Import({SecurityConfig.class, ObjectMapper.class})
public class RuleControllerTest extends ControllerTest {

    @MockitoBean
    private RuleService ruleService;

    @Test
    void shouldNotAllowCreateRuleIfNotAuthenticated() throws Exception {
        CreateRuleRequest dto = new CreateRuleRequest("Не шуметь после 23:00");

        mockMvc.perform(post("/api/apartments/1/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowCreateRuleIfNotInApartment() throws Exception {
        CreateRuleRequest dto = new CreateRuleRequest("Не шуметь после 23:00");

        mockMvc.perform(post("/api/apartments/1/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldNotAllowCreateRuleIfInhabitant() throws Exception {
        CreateRuleRequest dto = new CreateRuleRequest("Не шуметь после 23:00");

        mockMvc.perform(post("/api/apartments/1/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "ADMIN")
    void shouldAllowCreateRuleIfAdmin() throws Exception {
        CreateRuleRequest dto = new CreateRuleRequest("Не шуметь после 23:00");

        when(ruleService.create(any(User.class), anyInt(), any(CreateRuleRequest.class)))
                .thenReturn(new IdResponse<>(1L));

        mockMvc.perform(post("/api/apartments/1/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotAllowGetRulesIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/rules"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetRulesIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/rules"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetRulesIfInApartment() throws Exception {
        when(ruleService.get(anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/api/apartments/1/rules"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowDeleteRuleIfNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/rules/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowDeleteRuleIfNotInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/rules/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldNotAllowDeleteRuleIfInhabitant() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/rules/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "ADMIN")
    void shouldAllowDeleteRuleIfAdmin() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/rules/1"))
                .andExpect(status().isNoContent());
    }

}