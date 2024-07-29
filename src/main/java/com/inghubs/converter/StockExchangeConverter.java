package com.inghubs.converter;

import com.inghubs.dto.StockExchangeDTO;
import com.inghubs.entity.StockExchange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockExchangeConverter {

    private final StockConverter stockConverter;

    public StockExchangeDTO toDTO(StockExchange stockExchange) {
        return StockExchangeDTO.builder()
                .id(stockExchange.getId())
                .name(stockExchange.getName())
                .description(stockExchange.getDescription())
                .liveInMarket(stockExchange.isLiveInMarket())
                .stocks(stockExchange.getStocks().stream()
                        .map(stockConverter::toDTO)
                        .collect(Collectors.toSet()))
                .build();
    }

}