package com.inghubs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class StockExchangeDTO {
    private Long id;
    private String name;
    private String description;
    private boolean liveInMarket;
    private Set<StockDTO> stocks;
}