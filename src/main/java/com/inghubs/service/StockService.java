package com.inghubs.service;

import com.inghubs.dto.StockCreateDTO;
import com.inghubs.dto.StockDTO;
import com.inghubs.dto.StockPriceUpdateDTO;
import com.inghubs.entity.Stock;

public interface StockService {

    void deleteStock(Long id);

    StockDTO createStock(StockCreateDTO stockCreateDTO);

    StockDTO updateStockPrice(StockPriceUpdateDTO stockPriceUpdateDTO);

    Stock getStockById(Long id);
}