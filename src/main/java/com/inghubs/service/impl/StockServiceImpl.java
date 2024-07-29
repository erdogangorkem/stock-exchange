package com.inghubs.service.impl;

import com.inghubs.aspect.DataIntegrityRetryable;
import com.inghubs.aspect.OptimisticLockingRetryable;
import com.inghubs.converter.StockConverter;
import com.inghubs.dto.StockCreateDTO;
import com.inghubs.dto.StockDTO;
import com.inghubs.dto.StockPriceUpdateDTO;
import com.inghubs.entity.Stock;
import com.inghubs.exception.ResourceNotFoundException;
import com.inghubs.exception.StockAlreadyExistsException;
import com.inghubs.repository.StockRepository;
import com.inghubs.service.StockService;
import com.inghubs.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation for managing stocks.
 */
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StockConverter stockConverter;
    private final MessageUtils messageUtils;

    /**
     * Creates a new stock.
     *
     * @param stockCreateDTO the stock creation data transfer object
     * @return the created StockDTO
     * @throws StockAlreadyExistsException if a stock with the same name already exists
     */
    @DataIntegrityRetryable
    @Transactional
    public StockDTO createStock(StockCreateDTO stockCreateDTO) {
        Optional<Stock> existingStock = stockRepository.findByName(stockCreateDTO.getName());
        if (existingStock.isPresent()) {
            throw new StockAlreadyExistsException(messageUtils.getMessage("stock.already.exists", stockCreateDTO.getName()));
        }
        Stock stock = stockConverter.fromCreateDTO(stockCreateDTO);
        Stock savedStock = stockRepository.save(stock);
        return stockConverter.toDTO(savedStock);
    }

    /**
     * Updates the price of an existing stock.
     *
     * @param stockPriceUpdateDTO the stock price update data transfer object
     * @return the updated StockDTO
     * @throws ResourceNotFoundException if the stock with the given ID is not found
     */
    @OptimisticLockingRetryable
    @Transactional
    public StockDTO updateStockPrice(StockPriceUpdateDTO stockPriceUpdateDTO) {
        Stock existingStock = getStockById(stockPriceUpdateDTO.getId());
        existingStock.setCurrentPrice(stockPriceUpdateDTO.getCurrentPrice());
        Stock updatedStock = stockRepository.save(existingStock);
        return stockConverter.toDTO(updatedStock);
    }

    /**
     * Deletes a stock by its ID.
     *
     * @param id the ID of the stock to delete
     * @throws ResourceNotFoundException if the stock with the given ID is not found
     */
    @OptimisticLockingRetryable
    @Transactional
    public void deleteStock(Long id) {
        Stock stock = getStockById(id);
        stockRepository.delete(stock);
    }

    /**
     * Retrieves a stock by its ID.
     *
     * @param id the ID of the stock
     * @return the Stock entity
     * @throws ResourceNotFoundException if the stock with the given ID is not found
     */
    public Stock getStockById(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageUtils.getMessage("stock.not.found", id)));
    }
}