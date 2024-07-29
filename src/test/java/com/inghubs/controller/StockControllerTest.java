package com.inghubs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.dto.StockCreateDTO;
import com.inghubs.dto.StockDTO;
import com.inghubs.dto.StockPriceUpdateDTO;
import com.inghubs.entity.Stock;
import com.inghubs.entity.StockExchange;
import com.inghubs.exception.GlobalExceptionHandler;
import com.inghubs.exception.ResourceNotFoundException;
import com.inghubs.repository.StockExchangeRepository;
import com.inghubs.repository.StockRepository;
import com.inghubs.service.StockService;
import com.inghubs.util.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StockControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MessageUtils messageUtils;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockExchangeRepository stockExchangeRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private StockController stockController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stockController)
                .setControllerAdvice(new GlobalExceptionHandler(messageUtils)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void givenValidStockCreateDTO_whenCreateStock_thenReturnCreatedStock() throws Exception {
        StockCreateDTO stockCreateDTO = StockCreateDTO.builder()
                .name("Test Stock")
                .description("Test Description")
                .currentPrice(BigDecimal.valueOf(100.0))
                .build();

        StockDTO stockDTO = StockDTO.builder()
                .id(1L)
                .name("Test Stock")
                .description("Test Description")
                .currentPrice(BigDecimal.valueOf(100.0))
                .build();

        when(stockService.createStock(any(StockCreateDTO.class))).thenReturn(stockDTO);

        mockMvc.perform(post("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Stock"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.currentPrice").value(100.0));

        verify(stockService, times(1)).createStock(any(StockCreateDTO.class));
    }

    @Test
    void givenValidStockPriceUpdateDTO_whenUpdateStockPrice_thenReturnUpdatedStock() throws Exception {
        StockPriceUpdateDTO stockPriceUpdateDTO = StockPriceUpdateDTO.builder()
                .id(1L)
                .currentPrice(BigDecimal.valueOf(150.0))
                .build();

        StockDTO updatedStockDTO = StockDTO.builder()
                .id(1L)
                .name("Test Stock")
                .description("Test Description")
                .currentPrice(BigDecimal.valueOf(150.0))
                .build();

        when(stockService.updateStockPrice(any(StockPriceUpdateDTO.class))).thenReturn(updatedStockDTO);

        mockMvc.perform(put("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockPriceUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Stock"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.currentPrice").value(150.0));

        verify(stockService, times(1)).updateStockPrice(any(StockPriceUpdateDTO.class));
    }

    @Test
    void givenValidStockId_whenDeleteStock_thenReturnNoContent() throws Exception {
        Long stockId = 1L;
        doNothing().when(stockService).deleteStock(stockId);

        mockMvc.perform(delete("/api/v1/stock/{id}", stockId))
                .andExpect(status().isNoContent());

        verify(stockService, times(1)).deleteStock(stockId);
    }

    @Test
    void givenInvalidStockCreateDTO_whenCreateStock_thenReturnBadRequest() throws Exception {
        StockCreateDTO stockCreateDTO = StockCreateDTO.builder().build(); // Missing required fields

        mockMvc.perform(post("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void givenNonExistentStockId_whenUpdateStockPrice_thenThrowResourceNotFoundException() throws Exception {
        StockPriceUpdateDTO stockPriceUpdateDTO = StockPriceUpdateDTO.builder()
                .id(1L)
                .currentPrice(BigDecimal.valueOf(150.0))
                .build();

        String errorMessage = "Stock not found";
        when(stockService.updateStockPrice(any(StockPriceUpdateDTO.class))).thenThrow(new ResourceNotFoundException(errorMessage));
        when(messageUtils.getMessage(anyString(), any())).thenReturn(errorMessage);

        mockMvc.perform(put("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockPriceUpdateDTO)))
                .andExpect(status().isNotFound());

        verify(stockService, times(1)).updateStockPrice(any(StockPriceUpdateDTO.class));
    }

    @Test
    void givenNonExistentStockId_whenDeleteStock_thenThrowResourceNotFoundException() throws Exception {
        Long stockId = 1L;
        String errorMessage = "Stock not found";
        doThrow(new ResourceNotFoundException(errorMessage)).when(stockService).deleteStock(stockId);
        when(messageUtils.getMessage(anyString(), any())).thenReturn(errorMessage);

        mockMvc.perform(delete("/api/v1/stock/{id}", stockId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals(errorMessage, result.getResolvedException().getMessage()));

        verify(stockService, times(1)).deleteStock(stockId);
    }

    @Test
    void givenInvalidStockPriceUpdateDTO_whenUpdateStockPrice_thenReturnBadRequest() throws Exception {
        StockPriceUpdateDTO stockPriceUpdateDTO = StockPriceUpdateDTO.builder().build(); // Missing required fields

        mockMvc.perform(put("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockPriceUpdateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }
}