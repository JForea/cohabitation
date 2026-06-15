package com.example.backend.controllers;

import com.example.backend.annotations.WithMockCustomUser;
import com.example.backend.configurations.SecurityConfig;
import com.example.backend.dtos.in.buyings.CreateBuyingDto;
import com.example.backend.dtos.in.buyings.CreateBuyingShortDto;
import com.example.backend.dtos.in.buyings.CreateManyBuyingsDto;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.common.StatusResponse;
import com.example.backend.entities.User;
import com.example.backend.services.BuyingService;
import com.example.backend.types.BuyingCategory;
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

@WebMvcTest(BuyingController.class)
@Import({SecurityConfig.class, ObjectMapper.class})
public class BuyingControllerTest extends ControllerTest {

    @MockitoBean
    private BuyingService buyingService;

    @Test
    void shouldNotAllowCreateBuyingIfNotAuthenticated() throws Exception {
        CreateBuyingDto dto = new CreateBuyingDto(
                null,
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        mockMvc.perform(post("/api/apartments/1/buyings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowCreateBuyingIfNotInApartment() throws Exception {
        CreateBuyingDto dto = new CreateBuyingDto(
                null,
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        mockMvc.perform(post("/api/apartments/1/buyings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowCreateBuyingIfInApartment() throws Exception {
        CreateBuyingDto dto = new CreateBuyingDto(
                null,
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY,
                true
        );

        when(buyingService.create(any(User.class), any(CreateBuyingDto.class)))
                .thenReturn(new IdResponse<>(1L));

        mockMvc.perform(post("/api/apartments/1/buyings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotAllowCreateManyBuyingsIfNotAuthenticated() throws Exception {
        CreateBuyingShortDto buying = new CreateBuyingShortDto(
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY
        );

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(buying),
                null,
                true
        );

        mockMvc.perform(post("/api/apartments/1/buyings/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowCreateManyBuyingsIfNotInApartment() throws Exception {
        CreateBuyingShortDto buying = new CreateBuyingShortDto(
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY
        );

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(buying),
                null,
                true
        );

        mockMvc.perform(post("/api/apartments/1/buyings/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowCreateManyBuyingsIfInApartment() throws Exception {
        CreateBuyingShortDto buying = new CreateBuyingShortDto(
                "Хлеб",
                "1 шт",
                BuyingCategory.BAKERY
        );

        CreateManyBuyingsDto dto = new CreateManyBuyingsDto(
                List.of(buying),
                null,
                true
        );

        when(buyingService.createMany(any(User.class), any(CreateManyBuyingsDto.class)))
                .thenReturn(List.of(new IdResponse<>(1L)));

        mockMvc.perform(post("/api/apartments/1/buyings/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotAllowGetBuyingsIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/apartments/1/buyings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowGetBuyingsIfNotInApartment() throws Exception {
        mockMvc.perform(get("/api/apartments/1/buyings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowGetBuyingsIfInApartment() throws Exception {
        when(buyingService.get(anyInt(), any(User.class), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/apartments/1/buyings"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowChangeBuyingStatusIfNotAuthenticated() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/buyings/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowChangeBuyingStatusIfNotInApartment() throws Exception {
        mockMvc.perform(patch("/api/apartments/1/buyings/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowChangeBuyingStatusIfInApartment() throws Exception {
        StatusResponse response = mock(StatusResponse.class);

        when(buyingService.changeStatus(anyInt(), any(User.class), anyLong()))
                .thenReturn(response);

        mockMvc.perform(patch("/api/apartments/1/buyings/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowDeleteOneBuyingIfNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/buyings/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowDeleteOneBuyingIfNotInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/buyings/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowDeleteOneBuyingIfInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/buyings/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowDeleteManyBuyingsIfNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/buyings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(role = "HOUSELESS")
    void shouldNotAllowDeleteManyBuyingsIfNotInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/buyings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser
    void shouldAllowDeleteManyBuyingsIfInApartment() throws Exception {
        mockMvc.perform(delete("/api/apartments/1/buyings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isNoContent());
    }

}