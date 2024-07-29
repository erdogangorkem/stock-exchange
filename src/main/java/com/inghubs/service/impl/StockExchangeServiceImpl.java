package com.inghubs.service.impl;

import com.inghubs.aspect.OptimisticLockingRetryable;
import com.inghubs.converter.StockExchangeConverter;
import com.inghubs.dto.StockExchangeDTO;
import com.inghubs.entity.Stock;
import com.inghubs.entity.StockExchange;
import com.inghubs.exception.ResourceNotFoundException;
import com.inghubs.exception.StockAlreadyExistsException;
import com.inghubs.repository.StockExchangeRepository;
import com.inghubs.repository.StockRepository;
import com.inghubs.service.StockExchangeService;
import com.inghubs.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing stock exchanges.
 */
@Service
@RequiredArgsConstructor
public class StockExchangeServiceImpl implements StockExchangeService {

    private final StockExchangeRepository stockExchangeRepository;
    private final StockRepository stockRepository;
    private final StockExchangeConverter stockExchangeConverter;
    private final MessageUtils messageUtils;

    /**
     * Retrieves a StockExchange by its name.
     *
     * @param name the name of the StockExchange
     * @return the StockExchangeDTO
     */
    @Transactional
    public StockExchangeDTO getStockExchange(String name) {
        StockExchange stockExchange = getStockExchangeByName(name);
        return stockExchangeConverter.toDTO(stockExchange);
    }

    /**
     * Adds a Stock to a StockExchange.
     *
     * @param name the name of the StockExchange
     * @param stockId the ID of the Stock to add
     * @return the updated StockExchangeDTO
     */
    @OptimisticLockingRetryable
    @Transactional
    public StockExchangeDTO addStockToStockExchange(String name, Long stockId) {
        StockExchange stockExchange = getStockExchangeByName(name);
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException(messageUtils.getMessage("stock.not.found", stockId)));

        if (stockExchange.getStocks().contains(stock)) {
            throw new StockAlreadyExistsException(messageUtils.getMessage("stock.already.exists.in.exchange", stockId));
        }
        stockExchange.addStock(stock);
        StockExchange updatedStockExchange = stockExchangeRepository.save(stockExchange);
        return stockExchangeConverter.toDTO(updatedStockExchange);
    }

    /**
     * Removes a Stock from a StockExchange.
     *
     * @param name the name of the StockExchange
     * @param stockId the ID of the Stock to remove
     * @return the updated StockExchangeDTO
     */
    @OptimisticLockingRetryable
    @Transactional
    public StockExchangeDTO removeStockFromStockExchange(String name, Long stockId) {
        StockExchange stockExchange = getStockExchangeByName(name);
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException(messageUtils.getMessage("stock.not.found", stockId)));

        if (!stockExchange.getStocks().contains(stock)) {
            throw new ResourceNotFoundException(messageUtils.getMessage("stock.not.found.in.exchange"));
        }

        stockExchange.removeStock(stock);

        StockExchange updatedStockExchange = stockExchangeRepository.save(stockExchange);
        return stockExchangeConverter.toDTO(updatedStockExchange);
    }

    /**
     * Retrieves a StockExchange by its name.
     *
     * @param name the name of the StockExchange
     * @return the StockExchange entity
     */
    public StockExchange getStockExchangeByName(String name) {
        return stockExchangeRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException(messageUtils.getMessage("stock.exchange.not.found", name)));
    }

}