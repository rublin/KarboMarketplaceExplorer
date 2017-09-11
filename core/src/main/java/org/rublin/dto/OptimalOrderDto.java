package org.rublin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OptimalOrderDto {
    private String marketplace;
    private BigDecimal amountToSale;
    private BigDecimal amountToBuy;
    private BigDecimal rate;
}
