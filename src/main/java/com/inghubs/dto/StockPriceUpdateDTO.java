package com.inghubs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockPriceUpdateDTO {


    @NotNull(message = "{stock.id.not-null}")
    @Positive(message = "{stock.id.positive}")
    private Long id;

    @NotNull(message = "{stock.currentprice.not-null}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{stock.currentprice.positive}")
    @Digits(integer = 15, fraction = 2, message = "{stock.currentprice.digits}")
    private BigDecimal currentPrice;
}
