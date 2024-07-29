package com.inghubs.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.dto.StockCreateDTO;
import com.inghubs.dto.StockPriceUpdateDTO;
import com.inghubs.entity.Stock;
import com.inghubs.repository.StockExchangeRepository;
import com.inghubs.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class StockIntegrationTest {

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
        stockRepository.deleteAll();
        stockExchangeRepository.deleteAll();
    }

    @AfterEach
    void tearDownAfterEach() {
        stockRepository.deleteAll();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void whenCreateStock_thenCreated() throws Exception {
        StockCreateDTO stockCreateDTO = new StockCreateDTO("Test Stock", "Test Description", BigDecimal.valueOf(100.0));
        performPostRequest("/api/v1/stock", stockCreateDTO);

        Stock createdStock = stockRepository.findByName("Test Stock").orElse(null);
        assertThat(createdStock).isNotNull();
        assertThat(createdStock.getDescription()).isEqualTo("Test Description");
        assertThat(createdStock.getCurrentPrice()).isEqualByComparingTo(BigDecimal.valueOf(100.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenUpdateStockPrice_thenUpdated() throws Exception {
        Stock stock = createStock("Existing Stock", "Existing Description", BigDecimal.valueOf(100.0));
        StockPriceUpdateDTO stockPriceUpdateDTO = new StockPriceUpdateDTO(stock.getId(), BigDecimal.valueOf(150.0));
        performPutRequest("/api/v1/stock", stockPriceUpdateDTO);

        Stock updatedStock = stockRepository.findById(stock.getId()).orElse(null);
        assertThat(updatedStock).isNotNull();
        assertThat(updatedStock.getCurrentPrice()).isEqualByComparingTo(BigDecimal.valueOf(150.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenDeleteStock_thenDeleted() throws Exception {
        Stock stock = createStock("Stock to Delete", "Description to Delete", BigDecimal.valueOf(100.0));
        performDeleteRequest("/api/v1/stock/" + stock.getId());

        Stock deletedStock = stockRepository.findById(stock.getId()).orElse(null);
        assertThat(deletedStock).isNull();
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenUserRoleCreateStock_thenForbidden() throws Exception {
        StockCreateDTO stockCreateDTO = new StockCreateDTO("Test Stock", "Test Description", BigDecimal.valueOf(100.0));

        mockMvc.perform(post("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockCreateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenUserRoleUpdateStockPrice_thenForbidden() throws Exception {
        Stock stock = createStock("Existing Stock", "Existing Description", BigDecimal.valueOf(100.0));
        StockPriceUpdateDTO stockPriceUpdateDTO = new StockPriceUpdateDTO(stock.getId(), BigDecimal.valueOf(150.0));

        mockMvc.perform(put("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockPriceUpdateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenUserRoleDeleteStock_thenForbidden() throws Exception {
        Stock stock = createStock("Stock to Delete", "Description to Delete", BigDecimal.valueOf(100.0));

        mockMvc.perform(delete("/api/v1/stock/" + stock.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private Stock createStock(String name, String description, BigDecimal price) {
        Stock stock = new Stock();
        stock.setName(name);
        stock.setDescription(description);
        stock.setCurrentPrice(price);
        return stockRepository.save(stock);
    }

    private void performPostRequest(String url, Object content) throws Exception {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(content)))
                .andExpect(status().isCreated());
    }

    private void performPutRequest(String url, Object content) throws Exception {
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(content)))
                .andExpect(status().isOk());
    }

    private void performDeleteRequest(String url) throws Exception {
        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}