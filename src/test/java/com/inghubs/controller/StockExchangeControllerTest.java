package com.inghubs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.dto.StockExchangeDTO;
import com.inghubs.exception.GlobalExceptionHandler;
import com.inghubs.exception.ResourceNotFoundException;
import com.inghubs.service.StockExchangeService;
import com.inghubs.util.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StockExchangeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StockExchangeService stockExchangeService;

    @Mock
    private MessageUtils messageUtils;

    @InjectMocks
    private StockExchangeController stockExchangeController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stockExchangeController)
                .setControllerAdvice(new GlobalExceptionHandler(messageUtils))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void givenValidName_whenGetStockExchange_thenReturnStockExchangeDTO() throws Exception {
        String name = "Test Exchange";
        StockExchangeDTO stockExchangeDTO = StockExchangeDTO.builder()
                .id(1L)
                .name(name)
                .build();

        when(stockExchangeService.getStockExchange(anyString())).thenReturn(stockExchangeDTO);

        mockMvc.perform(get("/api/v1/stock-exchange/{name}", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(name));

        verify(stockExchangeService, times(1)).getStockExchange(anyString());
    }

    @Test
    void givenNonExistentName_whenGetStockExchange_thenThrowResourceNotFoundException() throws Exception {
        String name = "Non Existent Exchange";
        String errorMessage = "Stock exchange not found";
        when(stockExchangeService.getStockExchange(anyString())).thenThrow(new ResourceNotFoundException(errorMessage));
        when(messageUtils.getMessage(anyString(), any())).thenReturn(errorMessage);

        mockMvc.perform(get("/api/v1/stock-exchange/{name}", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals(errorMessage, result.getResolvedException().getMessage()));

        verify(stockExchangeService, times(1)).getStockExchange(anyString());
    }

    @Test
    void givenValidNameAndStockId_whenAddStockToStockExchange_thenReturnUpdatedStockExchangeDTO() throws Exception {
        String name = "Test Exchange";
        Long stockId = 1L;
        StockExchangeDTO stockExchangeDTO = StockExchangeDTO.builder()
                .id(1L)
                .name(name)
                .build();

        when(stockExchangeService.addStockToStockExchange(anyString(), anyLong())).thenReturn(stockExchangeDTO);

        mockMvc.perform(post("/api/v1/stock-exchange/{name}", name)
                        .param("stockId", String.valueOf(stockId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(name));

        verify(stockExchangeService, times(1)).addStockToStockExchange(anyString(), anyLong());
    }

    @Test
    void givenNonExistentNameOrStockId_whenAddStockToStockExchange_thenThrowResourceNotFoundException() throws Exception {
        String name = "Test Exchange";
        Long stockId = 1L;
        String errorMessage = "Stock exchange or stock not found";
        when(stockExchangeService.addStockToStockExchange(anyString(), anyLong())).thenThrow(new ResourceNotFoundException(errorMessage));
        when(messageUtils.getMessage(anyString(), any())).thenReturn(errorMessage);

        mockMvc.perform(post("/api/v1/stock-exchange/{name}", name)
                        .param("stockId", String.valueOf(stockId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals(errorMessage, result.getResolvedException().getMessage()));

        verify(stockExchangeService, times(1)).addStockToStockExchange(anyString(), anyLong());
    }

    @Test
    void givenValidNameAndStockId_whenRemoveStockFromStockExchange_thenReturnUpdatedStockExchangeDTO() throws Exception {
        String name = "Test Exchange";
        Long stockId = 1L;
        StockExchangeDTO stockExchangeDTO = StockExchangeDTO.builder()
                .id(1L)
                .name(name)
                .build();

        when(stockExchangeService.removeStockFromStockExchange(anyString(), anyLong())).thenReturn(stockExchangeDTO);

        mockMvc.perform(delete("/api/v1/stock-exchange/{name}", name)
                        .param("stockId", String.valueOf(stockId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(name));

        verify(stockExchangeService, times(1)).removeStockFromStockExchange(anyString(), anyLong());
    }

    @Test
    void givenNonExistentNameOrStockId_whenRemoveStockFromStockExchange_thenThrowResourceNotFoundException() throws Exception {
        String name = "Test Exchange";
        Long stockId = 1L;
        String errorMessage = "Stock exchange or stock not found";
        when(stockExchangeService.removeStockFromStockExchange(anyString(), anyLong())).thenThrow(new ResourceNotFoundException(errorMessage));
        when(messageUtils.getMessage(anyString(), any())).thenReturn(errorMessage);

        mockMvc.perform(delete("/api/v1/stock-exchange/{name}", name)
                        .param("stockId", String.valueOf(stockId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals(errorMessage, result.getResolvedException().getMessage()));

        verify(stockExchangeService, times(1)).removeStockFromStockExchange(anyString(), anyLong());
    }
}
