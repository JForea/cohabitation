package com.example.backend.controllers;

import com.example.backend.annotations.WithMockCustomUser;
import com.example.backend.configurations.SecurityConfig;
import com.example.backend.intefaces.FileStorage;
import com.example.backend.services.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
@Import({SecurityConfig.class, ObjectMapper.class})
public class ExpenseControllerTest extends ControllerTest {

    @MockitoBean
    private ExpenseService expenseService;

    @MockitoBean
    private FileStorage fileStorage;

    @Test
    void shouldNotAllowCreateExpenseIfNotAuthenticated() throws Exception {
        MockMultipartFile data = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                """
                {
                  "name": "Продукты",
                  "amount": 1000,
                  "category": "FOOD"
                }
                """.getBytes()
        );

        mockMvc.perform(multipart("/api/apartments/1/expenses")
                        .file(data))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowCreateExpenseIfNotInApartment() throws Exception {
        MockMultipartFile data = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                """
                {
                  "name": "Продукты",
                  "amount": 1000,
                  "category": "FOOD"
                }
                """.getBytes()
        );

        mockMvc.perform(multipart("/api/apartments/1/expenses")
                        .file(data))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotAllowGetExpensesIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/expenses")
                        .param("period", "2026-06"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetExpensesIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/expenses")
                        .param("period", "2026-06"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetExpensesIfInApartment() throws Exception {
        when(expenseService.get(
                anyInt(),
                anyShort(),
                anyShort(),
                any(),
                any(),
                any(YearMonth.class)
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/apartments/1/expenses")
                        .param("period", "2026-06"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowGetAmountIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/expenses/amount"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetAmountIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/expenses/amount"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetAmountIfInApartment() throws Exception {
        when(expenseService.getAmount(anyInt())).thenReturn(1);

        mockMvc.perform(get("/api/apartments/1/expenses/amount"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowGetSumByCategoryIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/expenses/stats/categories")
                        .param("period", "2026-06"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetSumByCategoryIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/expenses/stats/categories")
                        .param("period", "2026-06"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetSumByCategoryIfInApartment() throws Exception {
        when(expenseService.getSumByCategory(anyInt(), any(YearMonth.class)))
                .thenReturn(Map.of());

        mockMvc.perform(get("/api/apartments/1/expenses/stats/categories")
                        .param("period", "2026-06"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowDeleteManyExpensesIfNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowDeleteManyExpensesIfNotInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowDeleteManyExpensesIfInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isNoContent());
    }

}