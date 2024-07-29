package com.inghubs.service;

import com.inghubs.dto.StockExchangeDTO;
import com.inghubs.entity.StockExchange;

public interface StockExchangeService {

    StockExchangeDTO getStockExchange(String name);

    StockExchangeDTO addStockToStockExchange(String name, Long stockId);

    StockExchangeDTO removeStockFromStockExchange(String name, Long stockId);

    StockExchange getStockExchangeByName(String name);

}