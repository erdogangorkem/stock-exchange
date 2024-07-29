package com.inghubs.converter;

import com.inghubs.dto.StockCreateDTO;
import com.inghubs.dto.StockDTO;
import com.inghubs.entity.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockConverter {

    public StockDTO toDTO(Stock stock) {
        return StockDTO.builder()
                .id(stock.getId())
                .name(stock.getName())
                .description(stock.getDescription())
                .currentPrice(stock.getCurrentPrice())
                .lastUpdate(stock.getLastUpdate())
                .build();
    }

    public Stock fromCreateDTO(StockCreateDTO dto) {
        return Stock.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .currentPrice(dto.getCurrentPrice())
                .build();
    }

    public Stock fromDTO(StockDTO dto) {
        return Stock.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .currentPrice(dto.getCurrentPrice())
                .lastUpdate(dto.getLastUpdate())
                .build();
    }
}