package com.inghubs.service.impl;

import com.inghubs.converter.StockExchangeConverter;
import com.inghubs.dto.StockExchangeDTO;
import com.inghubs.entity.Stock;
import com.inghubs.entity.StockExchange;
import com.inghubs.exception.ResourceNotFoundException;
import com.inghubs.exception.StockAlreadyExistsException;
import com.inghubs.repository.StockExchangeRepository;
import com.inghubs.repository.StockRepository;
import com.inghubs.util.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockExchangeServiceImplTest {

    @Mock
    private StockExchangeRepository stockExchangeRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockExchangeConverter stockExchangeConverter;

    @Mock
    private MessageUtils messageUtils;

    @InjectMocks
    private StockExchangeServiceImpl stockExchangeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidStockExchangeName_whenGetStockExchange_thenReturnStockExchangeDTO() {
        String name = "Test Exchange";
        StockExchange stockExchange = StockExchange.builder()
                .id(1L)
                .name(name)
                .build();

        StockExchangeDTO stockExchangeDTO = StockExchangeDTO.builder()
                .id(1L)
                .name(name)
                .build();

        when(stockExchangeRepository.findByName(name)).thenReturn(Optional.of(stockExchange));
        when(stockExchangeConverter.toDTO(stockExchange)).thenReturn(stockExchangeDTO);

        StockExchangeDTO result = stockExchangeService.getStockExchange(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(stockExchangeRepository, times(1)).findByName(name);
        verify(stockExchangeConverter, times(1)).toDTO(stockExchange);
    }

    @Test
    void givenNonExistentStockExchangeName_whenGetStockExchange_thenThrowResourceNotFoundException() {
        String name = "Non Existent Exchange";
        when(stockExchangeRepository.findByName(name)).thenReturn(Optional.empty());
        when(messageUtils.getMessage("stock.exchange.not.found", new Object[]{name})).thenReturn("Stock exchange not found");

        assertThrows(ResourceNotFoundException.class, () -> stockExchangeService.getStockExchange(name));
        verify(stockExchangeRepository, times(1)).findByName(name);
        verify(messageUtils, times(1)).getMessage("stock.exchange.not.found", new Object[]{name});
    }

    @Test
    void givenValidStockExchangeNameAndStockId_whenAddStockToStockExchange_thenReturnUpdatedStockExchangeDTO() {
        String name = "Test Exchange";
        Long stockId = 1L;

        StockExchange stockExchange = StockExchange.builder()
                .id(1L)
                .name(name)
                .stocks(new HashSet<>())
                .build();

        Stock stock = Stock.builder()
                .id(stockId)
                .build();

        StockExchange updatedStockExchange = StockExchange.builder()
                .id(1L)
                .name(name)
                .stocks(new HashSet<>())
                .build();
        updatedStockExchange.addStock(stock);

        StockExchangeDTO updatedStockExchangeDTO = StockExchangeDTO.builder()
                .id(1L)
                .name(name)
                .build();

        when(stockExchangeRepository.findByName(name)).thenReturn(Optional.of(stockExchange));
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));
        when(stockExchangeRepository.save(any(StockExchange.class))).thenReturn(updatedStockExchange);
        when(stockExchangeConverter.toDTO(any(StockExchange.class))).thenReturn(updatedStockExchangeDTO);

        StockExchangeDTO result = stockExchangeService.addStockToStockExchange(name, stockId);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(stockExchangeRepository, times(1)).findByName(name);
        verify(stockRepository, times(1)).findById(stockId);
        verify(stockExchangeRepository, times(1)).save(any(StockExchange.class));
        verify(stockExchangeConverter, times(1)).toDTO(updatedStockExchange);
    }

    @Test
    void givenValidStockExchangeNameAndNonExistentStockId_whenAddStockToStockExchange_thenThrowIllegalArgumentException() {
        String name = "Test Exchange";
        Long stockId = 1L;

        StockExchange stockExchange = StockExchange.builder()
                .id(1L)
                .name(name)
                .build();

        when(stockExchangeRepository.findByName(name)).thenReturn(Optional.of(stockExchange));
        when(stockRepository.findById(stockId)).thenReturn(Optional.empty());
        when(messageUtils.getMessage("stock.not.found", new Object[]{stockId})).thenReturn("Stock not found");

        assertThrows(IllegalArgumentException.class, () -> stockExchangeService.addStockToStockExchange(name, stockId));
        verify(stockExchangeRepository, times(1)).findByName(name);
        verify(stockRepository, times(1)).findById(stockId);
        verify(messageUtils, times(1)).getMessage("stock.not.found", new Object[]{stockId});
    }

    @Test
    void givenValidStockExchangeNameAndExistingStockInExchange_whenAddStockToStockExchange_thenThrowStockAlreadyExistsException() {
        String name = "Test Exchange";
        Long stockId = 1L;

        Stock stock = Stock.builder()
                .id(stockId)
                .build();

        StockExchange stockExchange = StockExchange.builder()
                .id(1L)
                .name(name)
                .stocks(new HashSet<>())
                .build();
        stockExchange.addStock(stock);

        when(stockExchangeRepository.findByName(name)).thenReturn(Optional.of(stockExchange));
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));
        when(messageUtils.getMessage("stock.already.exists.in.exchange", new Object[]{stockId})).thenReturn("Stock already exists in exchange");

        assertThrows(StockAlreadyExistsException.class, () -> stockExchangeService.addStockToStockExchange(name, stockId));
        verify(stockExchangeRepository, times(1)).findByName(name);
        verify(stockRepository, times(1)).findById(stockId);
        verify(messageUtils, times(1)).getMessage("stock.already.exists.in.exchange", new Object[]{stockId});
    }

    @Test
    void givenValidStockExchangeNameAndStockId_whenRemoveStockFromStockExchange_thenReturnUpdatedStockExchangeDTO() {
        String name = "Test Exchange";
        Long stockId = 1L;

        Stock stock = Stock.builder()
                .id(stockId)
                .build();

        StockExchange stockExchange = StockExchange.builder()
                .id(1L)
                .name(name)
                .stocks(new HashSet<>())
                .build();
        stockExchange.addStock(stock);

        StockExchange updatedStockExchange = StockExchange.builder()
                .id(1L)
                .name(name)
                .build();

        StockExchangeDTO updatedStockExchangeDTO = StockExchangeDTO.builder()
                .id(1L)
                .name(name)
                .build();

        when(stockExchangeRepository.findByName(name)).thenReturn(Optional.of(stockExchange));
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));
        when(stockExchangeRepository.save(any(StockExchange.class))).thenReturn(updatedStockExchange);
        when(stockExchangeConverter.toDTO(any(StockExchange.class))).thenReturn(updatedStockExchangeDTO);

        StockExchangeDTO result = stockExchangeService.removeStockFromStockExchange(name, stockId);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(stockExchangeRepository, times(1)).findByName(name);
        verify(stockRepository, times(1)).findById(stockId);
        verify(stockExchangeRepository, times(1)).save(any(StockExchange.class));
        verify(stockExchangeConverter, times(1)).toDTO(updatedStockExchange);
    }

    @Test
    void givenValidStockExchangeNameAndNonExistentStockId_whenRemoveStockFromStockExchange_thenThrowIllegalArgumentException() {
        String name = "Test Exchange";
        Long stockId = 1L;

        StockExchange stockExchange = StockExchange.builder()
                .id(1L)
                .name(name)
                .build();

        when(stockExchangeRepository.findByName(name)).thenReturn(Optional.of(stockExchange));
        when(stockRepository.findById(stockId)).thenReturn(Optional.empty());
        when(messageUtils.getMessage("stock.not.found", new Object[]{stockId})).thenReturn("Stock not found");

        assertThrows(IllegalArgumentException.class, () -> stockExchangeService.removeStockFromStockExchange(name, stockId));
        verify(stockExchangeRepository, times(1)).findByName(name);
        verify(stockRepository, times(1)).findById(stockId);
        verify(messageUtils, times(1)).getMessage("stock.not.found", new Object[]{stockId});
    }

    @Test
    void givenValidStockExchangeNameAndNonExistentStockInExchange_whenRemoveStockFromStockExchange_thenThrowResourceNotFoundException() {
        String name = "Test Exchange";
        Long stockId = 1L;

        Stock stock = Stock.builder()
                .id(stockId)
                .build();

        StockExchange stockExchange = StockExchange.builder()
                .id(1L)
                .name(name)
                .stocks(new HashSet<>())
                .build();

        when(stockExchangeRepository.findByName(name)).thenReturn(Optional.of(stockExchange));
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));
        when(messageUtils.getMessage("stock.not.found.in.exchange", new Object[]{})).thenReturn("Stock not found in exchange");

        assertThrows(ResourceNotFoundException.class, () -> stockExchangeService.removeStockFromStockExchange(name, stockId));
        verify(stockExchangeRepository, times(1)).findByName(name);
        verify(stockRepository, times(1)).findById(stockId);
        verify(messageUtils, times(1)).getMessage("stock.not.found.in.exchange", new Object[]{});
    }

    @Test
    void givenStockExchangeWith4Stocks_whenAddStock_thenLiveInMarketIsTrue() {
        StockExchange stockExchange = createStockExchangeWithStocks(4, false);
        Stock newStock = createStock(5L, "Stock 5", "Description 5", BigDecimal.valueOf(105));

        when(stockExchangeRepository.findByName("Test Exchange")).thenReturn(Optional.of(stockExchange));
        when(stockRepository.findById(any(Long.class))).thenReturn(Optional.of(newStock));
        when(stockExchangeRepository.save(any(StockExchange.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stockExchangeConverter.toDTO(any(StockExchange.class))).thenReturn(new StockExchangeDTO());

        stockExchangeService.addStockToStockExchange("Test Exchange", newStock.getId());

        assertThat(stockExchange.isLiveInMarket()).isTrue();
        verify(stockExchangeRepository, times(1)).save(stockExchange);
    }

    @Test
    void givenStockExchangeWith5Stocks_whenRemoveStock_thenLiveInMarketIsFalse() {
        StockExchange stockExchange = createStockExchangeWithStocks(5, true);
        Stock stockToRemove = stockExchange.getStocks().iterator().next();

        when(stockExchangeRepository.findByName("Test Exchange")).thenReturn(Optional.of(stockExchange));
        when(stockRepository.findById(any(Long.class))).thenReturn(Optional.of(stockToRemove));
        when(stockExchangeRepository.save(any(StockExchange.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stockExchangeConverter.toDTO(any(StockExchange.class))).thenReturn(new StockExchangeDTO());

        stockExchangeService.removeStockFromStockExchange("Test Exchange", stockToRemove.getId());

        assertThat(stockExchange.isLiveInMarket()).isFalse();
        verify(stockExchangeRepository, times(1)).save(stockExchange);
    }

    private StockExchange createStockExchangeWithStocks(int numStocks, boolean liveInMarket) {
        Set<Stock> stocks = new HashSet<>();
        for (int i = 0; i < numStocks; i++) {
            stocks.add(Stock.builder()
                    .id((long) i)
                    .name("Stock " + i)
                    .description("Description " + i)
                    .currentPrice(BigDecimal.valueOf(100 + i))
                    .build());
        }

        return StockExchange.builder()
                .id(5L)
                .name("Test Exchange")
                .description("Test Description")
                .liveInMarket(liveInMarket)
                .stocks(stocks)
                .build();
    }

    private Stock createStock(long id, String name, String description, BigDecimal price) {
        return Stock.builder()
                .id(id)
                .name(name)
                .description(description)
                .currentPrice(price)
                .build();
    }

    @Test
    void whenTwoUsersAddSameStockConcurrently_thenOneSucceedsAndOneFails() throws InterruptedException {
        Set<Stock> stocks = new HashSet<>();
        Stock stockToAdd = Stock.builder()
                .id(1L)
                .name("Stock 1")
                .description("Description 1")
                .currentPrice(BigDecimal.valueOf(100))
                .build();

        StockExchange stockExchange = StockExchange.builder()
                .id(1L)
                .name("Test Exchange")
                .description("Test Description")
                .liveInMarket(false)
                .stocks(stocks)
                .build();

        when(stockExchangeRepository.findByName("Test Exchange")).thenReturn(Optional.of(stockExchange));
        when(stockRepository.findById(any(Long.class))).thenReturn(Optional.of(stockToAdd));
        when(stockExchangeRepository.save(any(StockExchange.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stockExchangeConverter.toDTO(any(StockExchange.class))).thenReturn(new StockExchangeDTO());
        when(messageUtils.getMessage(anyString(), any())).thenReturn("Stock already exists in exchange");

        CountDownLatch latch = new CountDownLatch(2);

        Thread user1 = new Thread(() -> {
            try {
                latch.countDown();
                latch.await();
                stockExchangeService.addStockToStockExchange("Test Exchange", stockToAdd.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread user2 = new Thread(() -> {
            try {
                latch.countDown();
                latch.await();
                assertThrows(StockAlreadyExistsException.class, () -> {
                    stockExchangeService.addStockToStockExchange("Test Exchange", stockToAdd.getId());
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        user1.start();
        user2.start();
        user1.join();
        user2.join();

        assertThat(stockExchange.getStocks()).contains(stockToAdd);
        verify(stockExchangeRepository, times(1)).save(stockExchange);
    }

}
