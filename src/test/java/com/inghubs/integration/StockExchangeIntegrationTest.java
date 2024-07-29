package com.inghubs.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.entity.Stock;
import com.inghubs.entity.StockExchange;
import com.inghubs.repository.StockExchangeRepository;
import com.inghubs.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class StockExchangeIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockExchangeRepository stockExchangeRepository;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @BeforeAll
    void setUpBeforeAll() {
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDownAfterEach() {
        stockExchangeRepository.deleteAll();
        stockRepository.deleteAll();
    }

    private Stock createStock(String name, String description, BigDecimal price) {
        Stock stock = new Stock();
        stock.setName(name);
        stock.setDescription(description);
        stock.setCurrentPrice(price);
        stock.setStockExchanges(new HashSet<>()); // Ensure the stocks set is initialized
        return stockRepository.save(stock);
    }

    private StockExchange createStockExchange(String name, String description) {
        StockExchange stockExchange = new StockExchange();
        stockExchange.setName(name);
        stockExchange.setDescription(description);
        stockExchange.setStocks(new HashSet<>());
        return stockExchangeRepository.save(stockExchange);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER"})
    @WithMockUser
    void whenGetStockExchange_thenOk() throws Exception {
        StockExchange stockExchange = createStockExchange("Test Exchange", "Test Description");

        mockMvc.perform(get("/api/v1/stock-exchange/" + stockExchange.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenAddStockToStockExchange_thenUpdated() throws Exception {
        StockExchange stockExchange = createStockExchange("Test Exchange", "Test Description");
        Stock stock = createStock("Test Stock", "Test Stock Description", BigDecimal.valueOf(100.0));

        mockMvc.perform(post("/api/v1/stock-exchange/" + stockExchange.getName() + "?stockId=" + stock.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        StockExchange updatedStockExchange = stockExchangeRepository.findById(stockExchange.getId()).orElse(null);
        assertThat(updatedStockExchange).isNotNull();
        assertThat(updatedStockExchange.getStocks()).contains(stock);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenRemoveStockFromStockExchange_thenUpdated() throws Exception {
        StockExchange stockExchange = createStockExchange("Test Exchange", "Test Description");
        Stock stock = createStock("Test Stock", "Test Stock Description", BigDecimal.valueOf(100.0));
        stockExchange.getStocks().add(stock);
        stockExchangeRepository.save(stockExchange);

        mockMvc.perform(delete("/api/v1/stock-exchange/" + stockExchange.getName() + "?stockId=" + stock.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        StockExchange updatedStockExchange = stockExchangeRepository.findById(stockExchange.getId()).orElse(null);
        assertThat(updatedStockExchange).isNotNull();
        assertThat(updatedStockExchange.getStocks()).doesNotContain(stock);
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenUserRoleAddStockToStockExchange_thenForbidden() throws Exception {
        StockExchange stockExchange = createStockExchange("Test Exchange", "Test Description");
        Stock stock = createStock("Test Stock", "Test Stock Description", BigDecimal.valueOf(100.0));

        mockMvc.perform(post("/api/v1/stock-exchange/" + stockExchange.getName() + "?stockId=" + stock.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenUserRoleRemoveStockFromStockExchange_thenForbidden() throws Exception {
        StockExchange stockExchange = createStockExchange("Test Exchange", "Test Description");
        Stock stock = createStock("Test Stock", "Test Stock Description", BigDecimal.valueOf(100.0));
        stockExchange.getStocks().add(stock);
        stockExchangeRepository.save(stockExchange);

        mockMvc.perform(delete("/api/v1/stock-exchange/" + stockExchange.getName() + "?stockId=" + stock.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}