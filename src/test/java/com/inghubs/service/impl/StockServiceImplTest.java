package com.inghubs.service.impl;

import com.inghubs.converter.StockConverter;
import com.inghubs.dto.StockCreateDTO;
import com.inghubs.dto.StockDTO;
import com.inghubs.dto.StockPriceUpdateDTO;
import com.inghubs.entity.Stock;
import com.inghubs.exception.ResourceNotFoundException;
import com.inghubs.repository.StockRepository;
import com.inghubs.service.impl.StockServiceImpl;
import com.inghubs.util.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockConverter stockConverter;

    @Mock
    private MessageUtils messageUtils;

    @InjectMocks
    private StockServiceImpl stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidStockCreateDTO_whenCreateStock_thenStockIsCreated() {
        StockCreateDTO stockCreateDTO = StockCreateDTO.builder()
                .name("Test Stock")
                .description("Test Description")
                .currentPrice(BigDecimal.valueOf(100.0))
                .build();

        Stock stock = Stock.builder()
                .id(1L)
                .name(stockCreateDTO.getName())
                .description(stockCreateDTO.getDescription())
                .currentPrice(stockCreateDTO.getCurrentPrice())
                .build();

        StockDTO stockDTO = StockDTO.builder()
                .id(stock.getId())
                .name(stock.getName())
                .description(stock.getDescription())
                .currentPrice(stock.getCurrentPrice())
                .build();

        when(stockConverter.fromCreateDTO(stockCreateDTO)).thenReturn(stock);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);
        when(stockConverter.toDTO(stock)).thenReturn(stockDTO);

        StockDTO createdStock = stockService.createStock(stockCreateDTO);

        assertNotNull(createdStock);
        assertEquals(stockCreateDTO.getName(), createdStock.getName());
        assertEquals(stockCreateDTO.getDescription(), createdStock.getDescription());
        assertEquals(stockCreateDTO.getCurrentPrice(), createdStock.getCurrentPrice());
        verify(stockConverter, times(1)).fromCreateDTO(stockCreateDTO);
        verify(stockRepository, times(1)).save(any(Stock.class));
        verify(stockConverter, times(1)).toDTO(stock);
    }

    @Test
    void givenNonExistentStockId_whenUpdateStockPrice_thenThrowResourceNotFoundException() {
        StockPriceUpdateDTO stockPriceUpdateDTO = StockPriceUpdateDTO.builder()
                .id(1L)
                .currentPrice(BigDecimal.valueOf(150.0))
                .build();

        when(stockRepository.findById(1L)).thenReturn(Optional.empty());
        when(messageUtils.getMessage("stock.not.found", new Object[]{1L})).thenReturn("Stock not found");

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> stockService.updateStockPrice(stockPriceUpdateDTO));
        verify(stockRepository, times(1)).findById(1L);
        verify(messageUtils, times(1)).getMessage("stock.not.found", new Object[]{1L});
    }

    @Test
    void givenValidStockPriceUpdateDTO_whenUpdateStockPrice_thenStockPriceIsUpdated() {
        StockPriceUpdateDTO stockPriceUpdateDTO = StockPriceUpdateDTO.builder()
                .id(1L)
                .currentPrice(BigDecimal.valueOf(150.0))
                .build();

        Stock stock = Stock.builder()
                .id(1L)
                .currentPrice(BigDecimal.valueOf(100.0))
                .build();

        Stock updatedStockEntity = Stock.builder()
                .id(1L)
                .currentPrice(BigDecimal.valueOf(150.0))
                .build();

        StockDTO updatedStockDTO = StockDTO.builder()
                .id(1L)
                .currentPrice(BigDecimal.valueOf(150.0))
                .build();

        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(Stock.class))).thenReturn(updatedStockEntity);
        when(stockConverter.toDTO(any(Stock.class))).thenReturn(updatedStockDTO);

        StockDTO updatedStock = stockService.updateStockPrice(stockPriceUpdateDTO);

        assertNotNull(updatedStock);
        assertEquals(BigDecimal.valueOf(150.0), updatedStock.getCurrentPrice());
        verify(stockRepository, times(1)).findById(1L);
        verify(stockRepository, times(1)).save(stock);
        verify(stockConverter, times(1)).toDTO(updatedStockEntity);
    }

    @Test
    void givenValidStockId_whenDeleteStock_thenStockIsDeleted() {
        Long stockId = 1L;

        Stock stock = Stock.builder()
                .id(stockId)
                .build();

        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));
        doNothing().when(stockRepository).delete(stock);

        stockService.deleteStock(stockId);

        verify(stockRepository, times(1)).findById(stockId);
        verify(stockRepository, times(1)).delete(stock);
    }

    @Test
    void givenNonExistentStockId_whenDeleteStock_thenThrowStockNotFoundException() {
        Long stockId = 1L;

        when(stockRepository.findById(stockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> stockService.deleteStock(stockId));
        verify(stockRepository, times(1)).findById(stockId);
    }
}