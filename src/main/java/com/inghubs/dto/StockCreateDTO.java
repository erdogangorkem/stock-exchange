package com.inghubs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockCreateDTO {

    @NotBlank(message = "{stock.name.not-blank}")
    @Size(min = 1, max = 250, message = "{stock.name.size}")
    private String name;

    @NotBlank(message = "{stock.description.not-blank}")
    @Size(max = 1024, message = "{stock.description.size}")
    private String description;

    @NotNull(message = "{stock.currentprice.not-null}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{stock.currentprice.positive}")
    @Digits(integer = 15, fraction = 2, message = "{stock.currentprice.digits}")
    private BigDecimal currentPrice;

}
